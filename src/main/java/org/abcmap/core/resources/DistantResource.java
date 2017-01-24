package org.abcmap.core.resources;

import org.abcmap.core.project.Project;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by remipassmoilesel on 22/01/17.
 */
public abstract class DistantResource {

    /**
     * Human readable name of resource
     *
     * @return
     */
    protected String name;

    /**
     * Human readable description of description
     */
    protected String description;

    public DistantResource(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Import this resource in specified project. This method may add layers, or something else ...
     *
     * @param p
     * @param progressListener
     * @throws IOException
     */
    public abstract void importIn(Project p, Consumer<DistantResourceProgressEvent> progressListener) throws IOException;

    /**
     * Get name and identifier of resource.
     * <p>
     * This name MUST be unique in its directory.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set name and identifier of resource.
     * <p>
     * This name MUST be unique in its directory.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get human readable description of this resource
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set human readable description of this resource
     *
     * @return
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DistantResource{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistantResource that = (DistantResource) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

}
