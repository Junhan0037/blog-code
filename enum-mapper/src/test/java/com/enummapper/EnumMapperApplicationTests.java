package com.enummapper;

import com.enummapper.after.EnumContract;
import com.enummapper.after.EnumModel;
import com.enummapper.before.Commission;
import com.enummapper.before.Contract;
import com.enummapper.before.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void enumModelType() {
        List<EnumModel> enumModels = new ArrayList<>();
        enumModels.add(EnumContract.CommissionType.MONEY);
        enumModels.add(EnumContract.CommissionCutting.CEIL);

        assertThat(enumModels.get(0).getValue(), is("money"));
        assertThat(enumModels.get(1).getValue(), is("ceil"));
    }

}
