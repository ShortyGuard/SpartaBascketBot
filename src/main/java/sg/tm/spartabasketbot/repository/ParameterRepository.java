package sg.tm.spartabasketbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.tm.spartabasketbot.model.Parameter;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long> {

}