package org.abcmap.core.resources;

import java.util.Objects;

/**
 * Created by remipassmoilesel on 22/01/17.
 */
public class DistantResource {


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

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
