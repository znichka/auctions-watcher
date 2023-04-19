package watcherbot.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import watcherbot.description.ItemDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log
public class ItemsService {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    public boolean insertIfUnique(ItemDescription item, int managerId) {
        return insertIfUnique(item.getId(), item.getPhotoHash(), item.getItemUrl(), managerId);
    }

    public boolean insertIfUnique(String itemId, String imageHash, String url, int managerId) {
        try
        {
            String sql = "INSERT INTO items (item_id, image_hash, manager_id, url) " +
                    "SELECT :item_id, :image_hash, :manager_id, :url " +
                    "WHERE NOT EXISTS (SELECT * FROM items WHERE manager_id = :manager_id and image_hash = :image_hash) " +
                    "ON CONFLICT DO NOTHING";

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("item_id", itemId);
            paramMap.put("image_hash", imageHash);
            paramMap.put("url", url);
            paramMap.put("manager_id", managerId);

            return jdbcTemplate.update(sql, paramMap) == 1;
        } catch (Exception e) {
            log.severe("Error while saving item to db. URL: " + url);
            throw e;
        }
    }
}
