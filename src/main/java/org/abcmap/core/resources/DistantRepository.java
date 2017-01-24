package org.abcmap.core.resources;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.gui.utils.GuiUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Predefined WMS server, which can be loaded from locale server list or distant.
 */
public class DistantRepository extends DistantResource {

    private static final CustomLogger logger = LogManager.getLogger(DistantRepository.class);

    private String url;

    public DistantRepository(String name, String url) {
        super(name, "");
        this.name = name;
        this.url = url;
    }

    @Override
    public void importIn(Project p, Consumer<DistantResourceProgressEvent> progressListener) throws IOException {

        GuiUtils.throwIfOnEDT();

        throw new IllegalStateException("Impossible to import a repository in a project: " + this);

    }

    /**
     * URL of repository
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL of repository
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DistantRepository that = (DistantRepository) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), url);
    }

    @Override
    public String toString() {
        return "DistantRepository{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
