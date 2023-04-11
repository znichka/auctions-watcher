package watcherbot.description;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "managers")
public class ManagerDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    int id;

    @Embedded
    TelegramBotCredentials credentials;

    @Column
    String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<PageDescription> pages = new ArrayList<>();


    public ManagerDescription setCredentials(TelegramBotCredentials credentials) {
        this.credentials = credentials;
        return this;
    }

    public ManagerDescription setName(String name) {
        this.name = name;
        return this;
    }

    public ManagerDescription addPages(List<PageDescription> pages) {
        if (this.pages == null) this.pages = new ArrayList<>();
        this.pages.addAll(pages);
        return this;
    }

    public ManagerDescription addPage(PageDescription page) {
        if (this.pages == null) this.pages = new ArrayList<>();
        this.pages.add(page);
        return this;
    }
}
