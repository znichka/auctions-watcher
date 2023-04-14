package watcherbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemsService {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    List<String> getItemIdsByManagerId(int managerId) {
        String sql = "SELECT item_id FROM items WHERE manager_id = :manager_id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("manager_id", managerId);
        return jdbcTemplate.queryForList(sql, paramMap, String.class);
    }

    public boolean register(String itemId, String imageHash, int managerId) {
        String sql = "INSERT INTO items (item_id, image_hash, manager_id) " +
                    "VALUES (:item_id, :image_hash, :manager_id) "
                + "ON CONFLICT DO NOTHING";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("item_id", itemId);
        paramMap.put("image_hash", imageHash);
        paramMap.put("manager_id", managerId);
        return jdbcTemplate.update(sql, paramMap) == 1;
    }
}
