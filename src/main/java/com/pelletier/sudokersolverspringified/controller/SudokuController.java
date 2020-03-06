package com.pelletier.sudokersolverspringified.controller;

import com.pelletier.sudokersolverspringified.model.SudokuPuzzle;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sudoku.elements.SudokuCellDataBase;
import sudoku.elements.SudokuCellDataBaseBuilder;
import sudoku.elements.SudokuCoordinate;
import sudoku.enums.EBoardType;
import sudoku.solving.utils.CommonUtils;
import sudoku.thread.SudokuThreadManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class SudokuController {

    @GetMapping(value = "/sudoku-solver", produces = MediaType.APPLICATION_JSON_VALUE)
    public SudokuPuzzle getPuzzle(){


        SudokuPuzzle sudokuPuzzle = new SudokuPuzzle();

        List<String> rows = new ArrayList<>();
        rows.add("2,1,6,5,3,,,,");
        rows.add("4,3,8,,,6,7,,");
        rows.add("7,9,5,,,,,,1");
        rows.add("3,,2,,,,,8,");
        rows.add(",,4,,8,,1,,");
        rows.add(",8,,,,,2,,9");
        rows.add("5,,,,,,,,7");
        rows.add(",,3,2,,,,5,");
        rows.add(",,,,4,5,8,,");

        sudokuPuzzle.setRows(rows);
        return sudokuPuzzle;
    }



    @PostMapping(value = "/sudoku-solver", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SudokuPuzzle solve(@RequestBody SudokuPuzzle sudokuPuzzle) throws Exception {

        List<List<String>> sudokuNumbers = new ArrayList<>();

        for(String row : sudokuPuzzle.getRows()){
            List<String> sudokuRow = Arrays.asList(row.split(",", -1));
            sudokuNumbers.add(sudokuRow);
        }


        SudokuCellDataBase sudokuCellDataBase =
                SudokuCellDataBaseBuilder.buildDataBase(sudokuNumbers, EBoardType.SUDOKU);
        CommonUtils.setCandidatesOnAllCells(sudokuCellDataBase);

        SudokuThreadManager sudokuThreadManager = new SudokuThreadManager(sudokuCellDataBase, 9);
        sudokuThreadManager.solve(20, TimeUnit.SECONDS);

        Thread.sleep(3000);


        for(int x = 0; x < 9; x++){
            List<String> row = new ArrayList<>();
            for(int y = 0; y < 9; y++){
                sudokuNumbers.get(x).set(y,sudokuCellDataBase.getCellValue(new SudokuCoordinate(x + 1, y + 1)));
                row.add(sudokuCellDataBase.getCellValue(new SudokuCoordinate(x + 1, y + 1)));
            }
            sudokuPuzzle.getRows().set(x, String.join(",", row));
        }

        return sudokuPuzzle;
    }

}
