# Java Enum 활용하기 ![Java_8](https://img.shields.io/badge/java-v8-red?logo=java) ![Spring_Boot](https://img.shields.io/badge/Spring_Boot-v2.4.3-green.svg?logo=spring)

[Java Enum 활용하기](https://www.notion.so/Java-Enum-26606566e3404b0ebb8dc517c8dc6fa9)

### 기본설정
예를 들어 **중개료 계약서 관리** 라는 시스템을 만든다고 하겠습니다.  
계약서의 항목은 다음과 같습니다.
* 회사명
* 수수료
* 수수료타입
    - 기록된 수수료를 %로 볼지, 실제 원단위의 금액으로 볼지를 나타냅니다.
* 수수료절삭
    - 수수료의 일정 자리수를 **반올림/올림/버림**할 것인지를 나타냅니다.

가장 쉽게 domain 클래스를 작성해보면 아래와 같습니다.

**Contract.java**

```java
@Entity
public class Contract {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private double commission; // 수수료

    @Column(nullable = false)
    private String commissionType; // 수수료 타입 (예: 퍼센테이지, 금액)

    @Column(nullable = false)
    private String commissionCutting; // 수수료 절삭 (예: 반올림, 올림, 버림)

    public Contract() {}

    public Contract(String company, double commission, String commissionType, String commissionCutting) {
        this.company = company;
        this.commission = commission;
        this.commissionType = commissionType;
        this.commissionCutting = commissionCutting;
    }

    public Long getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public double getCommission() {
        return commission;
    }

    public String getCommissionType() {
        return commissionType;
    }

    public String getCommissionCutting() {
        return commissionCutting;
    }
}
```

대부분이 String으로 이루어진 간단한 domain입니다.  
(company의 경우 이번 시간에 주요 항목이 아니기 때문에 별도의 테이블 분리 없이 문자열로 다루겠습니다. 원래는 테이블로 분리해야할 대상입니다^^)

domain클래스를 보시면 setter가 없습니다. 이는 의도한 것인데, getter와 달리 **setter는 무분별하게 생성하지 않습니다**.  
domain 인스턴스에 변경이 필요한 이벤트가 있을 경우 **그 이벤트를 나타낼 수 있는 메소드**를 만들어야하며, 무분별하게 값을 변경하는 setter는 최대한 멀리하시는게 좋습니다.  
(예를 들어, 주문취소 같은 경우 ```setOrderStatus()```가 아니라 ```cancelOrder()```를 만들어서 사용하는 것입니다.  
똑같이 orderStatus를 변경할지라도, 그 의도와 사용범위가 명확한 메소드를 만드는것이 중요합니다.)

그리고 이 domain을 관리할 repository를 생성하겠습니다.

**ContractRepository.java**

```java
public interface ContractRepository extends JpaRepository<Contract, Long>{
    Contract findByCommissionType(String commissionType);
    Contract findByCommissionCutting(String commissionCutting);
}
```

domain클래스와 repository클래스가 생성되었으니 간단한 테스트 클래스를 생성하겠습니다.

**ApplicationTests.java**

```java
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private ContractRepository repository;

	@Test
	public void add() {
		Contract contract = new Contract(
				"우아한짐카",
				1.0,
				"percent",
				"round"
		);
		repository.save(contract);
		Contract saved = repository.findAll().get(0);
		assertThat(saved.getCommission(), is(1.0));
	}
}

```

save & find가 잘되는 것을 확인할 수 있습니다.  
자 여기서부터 본격적으로 시작해보겠습니다.

### 문제 파악
위 코드를 토대로 시스템을 만든다고 생각해보시면 어떠실까요?  
몇가지 문제점이 보이시나요?  
생각하시는것과 다를수는 있지만, 제가 생각하기엔 다음과 같은 문제가 있어 보입니다.

* commissionType과 commissionCutting은 **IDE 지원을 받을 수 없다**.
    - 자동완성, 오타검증 등등
* commissionType과 commissionCutting의 **변경 범위가 너무 크다**.
    - 예를 들어, commissionType의 ```money```를 ```mount```로 변경해야 한다면 프로젝트 전체에서 ```money```를 찾아 변경해야 합니다.
    - 추가로 commissionType의 ```money``` 인지, 다른 domain의 ```money```인지 확인하는 과정도 추가되어 비용이 배로 들어가게 됩니다.
* commissionType과 commissionCutting에 잘못된 값이 할당되도 **검증하기가 어렵다**.
    - percent, money가 아닌 값이 할당되는 경우를 방지하기 위해 검증 메소드가 필요합니다.
* commissionType과 commissionCutting의 허용된 **값 범위를 파악하기 힘들다**.
    - 예를 들어, commissionType과 commissionCutting을 select box로 표기해야 한다고 생각해보겠습니다.
    - 이들의 가능한 값 리스트가 필요한데, 현재 형태로는 하드코딩 할 수 밖에 없습니다.

더 있을 수 있지만 위 4가지 문제가 바로 생각나는것 같습니다.  
그럼 이 문제들을 해결하기 위해서는 어떻게 코드를 수정하면 좋을까요?  
제일 먼저 떠오르는 방식은 **static 상수**입니다.

### 문제해결 - 1
보통 이렇게 고정된 값들이 필요한 경우 static 상수를 많이들 사용하십니다.  
그래서 static 상수로 먼저 문제해결을 시도해보겠습니다.

**Commission.java**

```java
public interface Commission {
    String TYPE_PERCENT = "percent";
    String TYPE_MONEY = "money";

    String CUTTING_ROUND = "round";
    String CUTTING_CEIL = "ceil";
    String CUTTING_FLOOR = "floor";
}
```

(인터페이스는 상수, 추상메소드만 허용가능하며 접근제한자는 public만 되므로 이런 상수모음에 적합한 형태입니다.)  
Commission 인터페이스를 통해서 테스트 코드를 작성해보겠습니다.

![static 자동완성](./images/static-자동완성.png)

(코드 작성중에 확인해보시면 이렇게 자동완성이 지원되는 것을 확인하실 수 있습니다.)

```java
	@Test
	public void add_staticVariable() {
		Contract contract = new Contract(
				"우아한짐카",
				1.0,
				Commission.TYPE_PERCENT,
				Commission.CUTTING_ROUND
		);

		repository.save(contract);
		Contract saved = repository.findAll().get(0);
		assertThat(saved.getCommission(), is(1.0));
	}
```

자 이렇게 ```static 상수``` 선언을 함으로써 IDE의 지원을 받을 수 있게 되었고, 혹시나 값을 변경할 일이 있어도 Commission 인터페이스의 값들만 변경하면 되므로
변경범위도 최소화 되었습니다.  
하지만 나머지 2가지 문제가 해결되지 않았습니다.
* 해당 시스템을 잘 모르는 사람의 경우 Commission 인터페이스의 값을 써야한다는걸 어떻게 알 수 있을까요?
    - 모르는 경우 ```"money"```로 직접 입력하는 경우를 막을 방법이 있을까요?
* commissionType, commissionCutting으로 select box를 출력시키려면 어떻게 해야할까요?

위 2가지 문제가 아직 해결되지 않았습니다.  
static 상수로는 결국 해결할 수 없기에 다른 방법을 시도해보겠습니다.  
그 방법이 바로 **enum**입니다.

### 문제해결 - 2
enum은 워낙 많은 Java 기본서에서 다루고 있기 때문에 enum에 대한 설명은 별도로 하지 않겠습니다.  
바로 코드를 작성해보겠습니다. 이전 코드는 남겨둔채로 진행해야 하기에 entity 클래스는 ```EnumContract```로 하겠습니다.

**EnumContract.java**
```java
    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // enum의 name을 DB에 저장하기 위해, 없을 경우 enum의 숫자가 들어간다.
    private CommissionType commissionType; // 수수료 타입 (예: 퍼센테이지, 금액)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommissionCutting commissionCutting; // 수수료 절삭 (예: 반올림, 올림, 버림)

    public enum CommissionType {

        PERCENT("percent"),
        MONEY("money");

        private String value;

        CommissionType(String value) {
            this.value = value;
        }

        public String getKey() {
            return name();
        }

        public String getValue() {
            return value;
        }
    }

    public enum CommissionCutting {
        ROUND("round"),
        CEIL("ceil"),
        FLOOR("floor");

        private String value;

        CommissionCutting(String value) {
            this.value = value;
        }

        public String getKey() {
            return name();
        }

        public String getValue() {
            return value;
        }
    }

```

domain 클래스의 다른 부분은 ```Contract```와 동일하며, 다른 부분만 작성하였습니다.  
이렇게 타입을 String에서 enum으로 변경하게 되면 CommissionType과 CommissionCutting은 제한된 범위내에서만 선택이 가능하게 됩니다.  
테스트 코드를 통해 DB 입출력 결과를 확인해보겠습니다.

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class EnumApplicationTests {

    @Autowired
    private EnumContractRepository enumContractRepository;

    @Test
    public void add() {
        enumContractRepository.save(new EnumContract(
                "우아한짐카",
                1.0,
                EnumContract.CommissionType.MONEY,
                EnumContract.CommissionCutting.ROUND));

        EnumContract saved = enumContractRepository.findOne(1L);

        assertThat(saved.getCommissionType(), is(EnumContract.CommissionType.MONEY));
        assertThat(saved.getCommissionCutting(), is(EnumContract.CommissionCutting.ROUND));
    }
}
```

![enum 값 확인](./images/enum-테스트값-확인.png)

enum을 타입으로 하여도 DB 입출력이 잘되는것을 확인할 수 있습니다.  
이젠 다른 개발자들이 개발을 진행할때도 타입 제한으로 **enum외에 다른 값들은 못받도록** 하였습니다.  
자 여기까지는 쉽게 온것 같습니다. 하지만! 마지막 문제인 commissionType, commissionCutting의 리스트를 보여주는 것은 어떻게 해야할까요?  
enum을 어떻게 잘 활용하면 될 것 같은 느낌이 들지 않으신가요?  
한번 진행해보겠습니다.

### Enum 관리 모듈
특정 enum 타입이 갖고 있는 모든 값을 출력시키는 기능은 Class의 ```getEnumConstants()``` 메소드를 사용하면 쉽게 해결할 수 있습니다.  
enum의 리스트는 select box 즉, view영역에 제공되어야 하기 때문에 Controller에서 전달하도록 만들어보겠습니다.

**ApiController.java**
```java
@RestController
public class ApiController {

    @GetMapping("/enum")
    public Map<String, Object> getEnum() {
        Map<String, Object> enums = new LinkedHashMap<>();

        Class commissionType = EnumContract.CommissionType.class;
        Class commissionCutting = EnumContract.CommissionCutting.class;

        enums.put("commissionType", commissionType.getEnumConstants());
        enums.put("commissionCutting", commissionCutting.getEnumConstants());
        return enums;
    }
}
```

commissionType과 commissionCutting의 class에서 ```getEnumConstants()```을 호출하여 map에 전달후, 리턴하도록 만든 간단한 메소드입니다. 바로 Boot를 실행시켜 결과값을 확인해보겠습니다.

![enum결과 1차](./images/enum결과1차.png)

각 enum의 전체 리스트는 출력되었는데 뭔가 부족하지 않으신가요?  
바로 enum의 **value값이 출력되지 않았습니다**.  
enum은 인스턴스가 아닌 **타입**입니다. 그래서 view로 전달되었을 때는 name만 남게 됩니다.   
이를 해결하기 위해 enum의 name과 value를 모두 가지는 Dto를 만들어보겠습니다.  
Dto를 만들기 전, 앞으로의 모든 enum들을 dto에서 사용할 수 있도록 인터페이스를 하나 만들겠습니다.  
해당 인터페이스의 이름은 ```EnumModel```이라 하겠습니다.  
**EnumModel.java**
```java
public interface EnumModel {
    String getKey();
    String getValue();
}
```
enum의 name(좀더 명확한 이름을 위해 key로 하였습니다.)과 value를 사용하기 위해 추상메소드를 추가하였습니다.   
그리고 CommissionType과 CommissionCutting이 이를 구현(implements)하도록 변경하겠습니다.

```java
public enum CommissionType implements EnumModel {

    PERCENT("percent"),
    MONEY("money");

    private String value;

    CommissionType(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}

public enum CommissionCutting implements EnumModel {
    ROUND("round"),
    CEIL("ceil"),
    FLOOR("floor");

    private String value;

    CommissionCutting(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
```

2개의 enum 타입이 모두 EnumModel을 구현하도록 변경하였습니다.  
Java의 **다형성**으로, 인터페이스를 구현하게 될 경우 CommissionType과 CommissionCutting이 EnumModel 타입으로 다룰수 있게 되었습니다.  
간단한 테스트 코드로 이를 확인해보겠습니다.

![EnumModel 테스트코드](./images/enumModel테스트.png)

자! 그럼 이 EnumModel을 이용하여 실제 값을 가지고 view에 전달할 수 있는 Dto를 만들겠습니다.   
Dto의 이름은 ```EnumValue```입니다.

**EnumValue.java**
```java
public class EnumValue {
    private String key;
    private String value;

    public EnumValue(EnumModel enumModel) {
        key = enumModel.getKey();
        value = enumModel.getValue();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

```

EnumValue는 생성자 인자로 위에서 만든 EnumModel을 받도록 하여 commissionType, commissionCutting 모두를 받을 수 있습니다.  
그럼 Controller에 EnumValue를 이용한 메소드를 추가해보겠습니다.

```java
@GetMapping("/value")
public Map<String, List<EnumValue>> getEnumValue() {
    Map<String, List<EnumValue>> enumValues = new LinkedHashMap<>();

    enumValues.put("commissionType", toEnumValues(EnumContract.CommissionType.class));
    enumValues.put("commissionCutting", toEnumValues(EnumContract.CommissionCutting.class));

    return enumValues;
}

private List<EnumValue> toEnumValues(Class<? extends EnumModel> e){
    /*
        // Java8이 아닐경우
        List<EnumValue> enumValues = new ArrayList<>();
        for (EnumModel enumType : e.getEnumConstants()) {
            enumValues.add(new EnumValue(enumType));
        }
        return enumValues;
     */
    return Arrays
            .stream(e.getEnumConstants())
            .map(EnumValue::new)
            .collect(Collectors.toList());
}
```

EnumModel 배열을 EnumValue 리스트로 전환하는 일을 하는 ```toEnumValues```를 구현하여 ```getEnumValue```에서는 해당 메소드를 호출만 하도록 변경하였습니다.  
```toEnumValues```의 경우 Java8의 stream을 사용하면 아주 깔끔한 코드로 전환이 가능하지만, 혹시나 아직 Java8 문법이 어색하시거나 그 이하 버전을 사용하실 경우를 대비하여 주석으로 하위버전 코드를 추가하였습니다.  
그럼 위 코드가 정상적으로 View에 전달되는지 확인해보겠습니다.

![enum 결과2차](./images/enum결과2차.png)

원하는 대로 key와 value가 나오는 것을 확인할 수 있습니다!  
그럼 이제 다 끝난걸까요? ```ApiController```는 더이상 수정할 부분이 없을까요?  
위 코드를 다시 보시면 실제로 사용하기에는 부족함이 많은 것을 알 수 있습니다.
* 매번 Controller를 호출할 때마다 EnumValue로 전환하는 작업을 수행해야 합니다.
* 다른 Controller/Service/Repository에서 enum의 리스트를 사용하고 싶을 경우 중복코드가 발생합니다.

위 2가지 문제를 해결해야 한다면 어떤 방법이 가장 먼저 떠오르시나요?  
아마 대부분 Spring의 ```Bean```으로 등록해야겠다는 생각이 드실것 같습니다.  
어플리케이션이 시작할때만 EnumValue로 전환하는 작업을 수행하고, 그 이후에는 이미 등록된 것들을 호출하여 원하는 곳에서 사용하면 될것 같습니다.  
enum 타입들을 관리하는 모듈의 이름을 ```EnumMapper```로 하여 개발을 진행하겠습니다.

**EnumMapper.java**

```java
public class EnumMapper {
    private Map<String, List<EnumValue>> factory = new HashMap<>();

    private List<EnumValue> toEnumValues(Class<? extends EnumModel> e){

            // Java8이 아닐경우
//            List<EnumValue> enumValues = new ArrayList<>();
//            for (EnumModel enumType : e.getEnumConstants()) {
//                enumValues.add(new EnumValue(enumType));
//            }
//            return enumValues;

        return Arrays
                .stream(e.getEnumConstants())
                .map(EnumValue::new)
                .collect(Collectors.toList());
    }

    public void put(String key, Class<? extends EnumModel> e){
        factory.put(key, toEnumValues(e));
    }

    public Map<String, List<EnumValue>> getAll(){
        return factory;
    }

    public Map<String, List<EnumValue>> get(String keys){

            // Java8이 아닐경우
//            Map<String, List<EnumValue>> result = new LinkedHashMap<>();
//            for (String key : keys.split(",")) {
//                result.put(key, factory.get(key));
//            }
//
//            return result;

        return Arrays
                .stream(keys.split(","))
                .collect(Collectors.toMap(Function.identity(), key -> factory.get(key)));
    }


}
```

혹시나 모든 enum 타입을 가져오는 것외에 지정한 enum만 가져오는 기능이 필요할 수도 있기에 ```get()```도 추가로 구현하였습니다.  
어플리케이션 내부에 저장하기 위해 ```factory map```를 생성하되, 다른 클래스에서 직접 접근하지 못하도록 ```private```으로 막았습니다.  
이렇게 할 경우 외부 클래스에서 접근하려면 ```public```으로 오픈한 ```put()```, ```get()```, ```getAll()```만 가능하기 때문에 ```toEnumValues```를 강제할 수가 있습니다.  
여기서 주의 깊게 보셔야 할 것은 **생성자에서 commissionType, commissionCutting을 등록하지 않은 점입니다.

이는 EnumMapper 자체가 **단독 모듈**로서 사용하기 위함인데, 만약 생성자에서 commissionType, commissionCutting을 추가하게 될 경우 다른 프로젝트에서 EnumMapper를 사용할 때에는 **EnumMapper 내부의 코드를 수정**해야 하는 일이 발생합니다. 이는 OCP원칙에 위반되기도 하며, 유지보수 하기가 어렵게 만드는 일이기 때문에 항상 공통 모듈을 만들때는 이 점을 주의해야 합니다.

이렇게 만든 EnumMapper를 ```Bean```으로 등록하겠습니다.  
**AppConfig.java**
```java
@Configuration
public class AppConfig {

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("commissionType", EnumContract.CommissionType.class);
        enumMapper.put("commissionCutting", EnumContract.CommissionCutting.class);
        return enumMapper;
    }
}
```

그리고 실제로 select box에서 사용할 수 있도록 value값도 select box의 타이틀로 변경하겠습니다.

**EnumContract.java**

```java
public enum CommissionType implements EnumModel {

    PERCENT("퍼센트"),
    MONEY("금액");

    private String value;

    CommissionType(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}

public enum CommissionCutting implements EnumModel {
    ROUND("반올림"),
    CEIL("올림"),
    FLOOR("버림");

    private String value;

    CommissionCutting(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
```

이제 모든 준비가 다 되었습니다!  
브라우저를 열어 ```http://localhost:8080/mapper```로 접근하겠습니다.

![enum결과 마지막](./images/enum결과마지막.png)

짠! 드디어 원하던 결과가 출력 되었습니다.  
이렇게 key와 value를 받게 되면 view에서는 select box를 그릴때 아래와 같이 아주 쉽게 코드를 구현할 수 있습니다.
```
<select class="form-control" id="selectCategories">
    {{#each categories}}
        <option value="{{key}}">{{value}}</option>
    {{/each}}
</select>
```

(handlebars.js의 코드를 예시로 들었습니다.)

key를 option의 value로, value를 출력 텍스트로 하여 한번에 그릴 수 있게하고, 다시 Controller로 전달할 경우에는 key값만 전달하여도 enum에 자동할당 되기 때문에 매끄럽게 타입전환이 이루어집니다.

어떠셨나요?  
조금 부족함이 많은 예제이지만 enum을 이럴때 쓰면 좋겠다 라는 생각이 작게나마 드셨다면 정말 만족할것 같습니다.  
작성하다보니 너무나 내용이 길어졌지만, 끝까지 읽어주셔서 정말 감사드립니다.  
다음에는 좀 더 알찬 내용으로 뵙겠습니다.  
감사합니다!

### 첨언
변경이 잦은 데이터일 경우 데이터베이스의 테이블로 관리하는 것이 좀 더 좋은 방법일 수 있습니다.  
다만, 변경이 거의 없는 데이터 그룹의 경우엔 enum이 더 좋은 방법이 될 수 있습니다.  
만약 위 기준만으로 결정하기가 힘들다면 2가지 방식의 장/단점을 보시고 결정하셔도 될것 같습니다.
* DB로 관리하게 될 경우, 변경에 용이하다는 장점을 얻지만 반면에 개발자가 개발/운영시에 전체 데이터를 한눈에 볼 수 없으며 컴파일 단계에서 검증하기가 어렵다는 단점이 있습니다.
* enum으로 관리하게 될 경우, 변경에는 DB때보다 어렵지만 (변경이 필요할 경우 배포가 필요하게 됨) 개발자가 개발/운영시에 한눈에 전체 데이터를 확인하고, 컴파일러에서 직접 체크가 가능하기 떄문에 실수할 여지가 줄어듭니다.  
