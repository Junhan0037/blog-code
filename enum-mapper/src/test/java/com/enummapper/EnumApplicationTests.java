package com.enummapper;

import com.enummapper.after.EnumContract;
import com.enummapper.after.EnumContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class EnumApplicationTests {

    @Autowired EnumContractRepository enumContractRepository;

    @Test
    public void add() {
        enumContractRepository.save(new EnumContract(
                "우아한짐카",
                1.0,
                EnumContract.CommissionType.MONEY,
                EnumContract.CommissionCutting.ROUND));

        EnumContract saved = enumContractRepository.findById(1L).get();

        assertThat(saved.getCommissionType(), is(EnumContract.CommissionType.MONEY));
        assertThat(saved.getCommissionCutting(), is(EnumContract.CommissionCutting.ROUND));
    }

}
