package model;

import javafx.collections.FXCollections;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Model{
    //fileArrayList
    private ObservableList<File> fileArrayList = FXCollections.observableArrayList();

    public synchronized ObservableList<File> getFileArrayList() {
        return fileArrayList;
    }
}
