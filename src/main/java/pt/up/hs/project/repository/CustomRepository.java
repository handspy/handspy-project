package pt.up.hs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    void refresh();
}
