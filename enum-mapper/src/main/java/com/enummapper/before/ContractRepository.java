package com.enummapper.before;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Contract findByCommissionType(String commissionType);

    Contract findByCommissionCutting(String commissionCutting);

}
