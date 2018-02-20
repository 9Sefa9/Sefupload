package model;

import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;

public class Model{
    //fileArrayList
    private ObservableList<File> fileArrayList = FXCollections.observableArrayList();

    public ObservableList<File> getFileArrayList() {
        return fileArrayList;
    }

    public void setFileArrayList(ObservableList<File> fileArrayList) {
        this.fileArrayList = fileArrayList;
    }
}
