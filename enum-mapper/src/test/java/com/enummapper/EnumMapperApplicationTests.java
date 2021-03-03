package com.enummapper;

import com.enummapper.before.Commission;
import com.enummapper.before.Contract;
import com.enummapper.before.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class EnumMapperApplicationTests {

    @Autowired ContractRepository contractRepository;

    @Test
    public void add() {
        Contract contract = new Contract(
                "우아한짐카",
                1.0,
                "percent",
                "round"
        );
        contractRepository.save(contract);
        Contract saved = contractRepository.findAll().get(0);
        assertThat(saved.getCommission(), is(1.0));
    }

    @Test
    public void add_staticVariable() {
        Contract contract = new Contract(
                "우아한짐카",
                1.0,
                Commission.TYPE_PERCENT,
                Commission.CUTTING_ROUND
        );

        contractRepository.save(contract);
        Contract saved = contractRepository.findAll().get(0);
        assertThat(saved.getCommission(), is(1.0));
    }

}
