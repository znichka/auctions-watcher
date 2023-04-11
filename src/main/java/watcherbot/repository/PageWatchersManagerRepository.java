package watcherbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import watcherbot.description.ManagerDescription;
import watcherbot.watchers.PageWatchersManager;

@Repository
public interface PageWatchersManagerRepository extends JpaRepository<ManagerDescription, Integer> {
}
