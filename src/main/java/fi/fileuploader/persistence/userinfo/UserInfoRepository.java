package fi.fileuploader.persistence.userinfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID>,
                                                QuerydslPredicateExecutor<UserInfo> {
}
