package main.java.com.alexa.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

/**
 * Created by dhanetwa on 4/14/2018.
 */
public class FlowsLibraryVO {
    private String id;
    private String parentId;
    private Boolean leaf;
    private String path;
    private String name;
    private String type;
    private String icon;
    private Boolean runnable;
    private Boolean graphicRepresentationCapable;
    private ArrayList<String> childrenIds;

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean getRunnable() {
        return runnable;
    }

    public void setRunnable(Boolean runnable) {
        this.runnable = runnable;
    }

    public Boolean getGraphicRepresentationCapable() {
        return graphicRepresentationCapable;
    }

    public void setGraphicRepresentationCapable(Boolean graphicRepresentationCapable) {
        this.graphicRepresentationCapable = graphicRepresentationCapable;
    }

    public ArrayList<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(ArrayList<String> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
