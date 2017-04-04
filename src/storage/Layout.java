package it.menzani.yiupp.storage;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class Layout {

    private int extendedState = JFrame.MAXIMIZED_BOTH;
    @Nullable
    private Point location;
    @Nullable
    private Point dimension;
    private int workbenchSeparator = -1;

    public int getWorkbenchSeparator() {
        return workbenchSeparator;
    }

    public void setWorkbenchSeparator(int workbenchSeparator) {
        this.workbenchSeparator = workbenchSeparator;
    }

    public int getExtendedState() {
        return extendedState;
    }

    public void setExtendedState(int extendedState) {
        this.extendedState = extendedState;
    }

    @Nullable
    public Point getLocation() {
        return location;
    }

    public void setLocation(@Nullable Point location) {
        this.location = location;
    }

    @Nullable
    public Point getDimension() {
        return dimension;
    }

    public void setDimension(@Nullable Point dimension) {
        this.dimension = dimension;
    }

}
