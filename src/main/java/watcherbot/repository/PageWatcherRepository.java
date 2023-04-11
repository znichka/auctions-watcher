package watcherbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import watcherbot.description.PageDescription;
import watcherbot.watchers.PageWatcher;

@Repository
public interface PageWatcherRepository extends JpaRepository<PageDescription, Integer> {
}
