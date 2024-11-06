package mmtk.projects.backend.email;


import lombok.Getter;

/**
 * Author : Min Myat Thu Kha
 * Created At : 03/11/2024, Nov ,14, 24
 * Project Name : BookSocialNetwork
 **/
@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");

    private final String name;

    EmailTemplateName(final String name) {
        this.name = name;
    }
}
