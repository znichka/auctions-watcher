package watcherbot.description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//@AllArgsConstructor
@Getter
@NoArgsConstructor
@Log
public class ItemDescription {
    String id;
    String itemUrl;
    @Setter
    String photoUrl;
    String caption;
    @JsonIgnore
    byte[] photoContents;
    @JsonIgnore
    String photoHash;

    public ItemDescription(String id, String itemUrl, String photoUrl, String caption) {
        this.id = id;
        this.itemUrl = itemUrl;
        this.photoUrl = photoUrl;
        this.caption = caption;
    }

    public byte[] getPhotoContents(){
        if (photoContents == null) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response
                    = restTemplate.getForEntity(photoUrl, byte[].class);

            if (response.getStatusCode().is2xxSuccessful())
                photoContents = response.getBody();
        }
        return photoContents;
    }

    public String getPhotoHash() {
        if (photoHash == null) {
            InputStream is = new ByteArrayInputStream(getPhotoContents());
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                DigestInputStream dis = new DigestInputStream(is, md);
                dis.readAllBytes();
                photoHash = new String(md.digest());
            } catch (NoSuchAlgorithmException | IOException e) {
                log.severe("Error while getting photo hash for item " + itemUrl);
            }
        }
        return photoHash;
    }
}
