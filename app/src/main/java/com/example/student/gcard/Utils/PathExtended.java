package com.example.student.gcard.Utils;

import android.graphics.Path;

import com.example.student.gcard.Square;

public class PathExtended {

    private Square _StartSquare;
    private Square _EndSquare;
    private Path _Path;

    public PathExtended(Square startSquare, Square endSquare, Path path) {
        _StartSquare = startSquare;
        _EndSquare = endSquare;
        _Path = path;
    }

    public PathExtended() {
    }

    public Square getStartSquare() {
        return _StartSquare;
    }

    public void setStartSquare(Square startSquare) {
        _StartSquare = startSquare;
    }

    public Square getEndSquare() {
        return _EndSquare;
    }

    public void setEndSquare(Square endSquare) {
        _EndSquare = endSquare;
    }

    public Path getPath() {
        return _Path;
    }

    public void setPath(Path path) {
        _Path = path;
    }
}
