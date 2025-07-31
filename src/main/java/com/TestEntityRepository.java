package com;

// TODO: Update the import below to match the actual package of TestEntity
// Example: import com.model.TestEntity;
// import com.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}