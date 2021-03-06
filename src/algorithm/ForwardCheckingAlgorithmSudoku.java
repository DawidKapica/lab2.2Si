package algorithm;

import com.sun.jdi.Value;
import heuristics.valueSelection.ValueSelection;
import heuristics.variableSelection.VariableSelection;
import informations.IndividualSudoku;
import informations.PointSudoku;
import structures.Node;
import structures.Tree;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ForwardCheckingAlgorithmSudoku {

    Tree<IndividualSudoku> tree;

    ValueSelection valueSelection;
    VariableSelection variableSelection;

    ArrayList<IndividualSudoku> solveSudoku = new ArrayList<IndividualSudoku>();

    int numberOfNodes = 0;
    int findAllSolutionsTime = 0;
    int numberOfReccurence = 0;

    boolean foundFirstSolution = false;

    int numberOfNodesFirstSol = 0;
    int findFirstSolTime = 0;
    long startTimeFirstSol;
    int numberOfReccurenceFirstSol = 0;

    public ForwardCheckingAlgorithmSudoku(ValueSelection valueSelection, VariableSelection variableSelection) {
        this.valueSelection = valueSelection;
        this.variableSelection = variableSelection;
    }

    public ArrayList<IndividualSudoku> findSolutions(IndividualSudoku individualSudoku) {

        long startTime = ZonedDateTime.now().toInstant().toEpochMilli();
        startTimeFirstSol = startTime;
        IndividualSudoku individualSudokucopy = new IndividualSudoku(individualSudoku);
//        individualSudokucopy.prepairDomain();
        tree = new Tree<IndividualSudoku>(new Node<IndividualSudoku>(individualSudokucopy));

        try {
            makeTree(tree.getRootNode());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        long endTime = ZonedDateTime.now().toInstant().toEpochMilli();

        findAllSolutionsTime = (int)(endTime - startTime);
        return solveSudoku;
    }

    public Node<IndividualSudoku> makeTree(Node<IndividualSudoku> node) throws CloneNotSupportedException {
        numberOfNodes = numberOfNodes + 1;

        PointSudoku pointSudoku = variableSelection.chooseVariable(node.getData());
        IndividualSudoku individualSudoku = node.getData().clone();
        IndividualSudoku individualSudokuChild = individualSudoku.clone();

        if(pointSudoku != null) {
            while (individualSudoku.getSingleElement(pointSudoku).getDomainValues().size()!= 0) {
                int value = valueSelection.chooseValue(individualSudoku, pointSudoku);

                individualSudoku.getSingleElement(pointSudoku).deleteValueDomain(value);
                individualSudokuChild.setSingleElement(pointSudoku, value);

                if ( individualSudokuChild.checkSudoku()) {

                    IndividualSudoku individualSudokuChildCopy = new IndividualSudoku(individualSudokuChild);

                    individualSudokuChildCopy.deleteDomainValuesGrid(pointSudoku, value);
                    individualSudokuChildCopy.deleteDomainValuesColumn(pointSudoku, value);
                    individualSudokuChildCopy.deleteDomainValuesRow(pointSudoku, value);

                    if(individualSudokuChildCopy.isHasFieldNullDomain() == false) {
                        Node<IndividualSudoku> nodeChild = new Node<IndividualSudoku>(new IndividualSudoku(individualSudokuChildCopy));
//                        nodeChild.setParent(node);
//                        node.addNodeChild(makeTree(nodeChild));
                        makeTree(nodeChild);

                    } else {
                        numberOfReccurence = numberOfReccurence + 1;
                    }
                } else {
                    numberOfReccurence = numberOfReccurence + 1;
                }
            }
        } else {
            solveSudoku.add(individualSudoku);
            numberOfNodes = numberOfNodes + 1;
            if (foundFirstSolution == false) {
                foundFirstSolution = true;
                long endTimeFirstSol = ZonedDateTime.now().toInstant().toEpochMilli();
                numberOfNodesFirstSol = numberOfNodes;
                numberOfReccurenceFirstSol = numberOfReccurence;
                findFirstSolTime = (int)(endTimeFirstSol - startTimeFirstSol);
            }
            return node;
        }
        return node;
    }

    public int getNumberOfNodes () {
        return numberOfNodes;
    }

    public int getFindAllSolutionsTime () {
        return findAllSolutionsTime;
    }

    public int getFindFirstSolTime () {
        return findFirstSolTime;
    }

    public int getNumberOfNodesFirstSol () {
        return numberOfNodesFirstSol;
    }

    public int getNumberOfReccurence () {
        return numberOfReccurence;
    }

    public int getNumberOfReccurenceFirstSol () {
        return numberOfReccurenceFirstSol;
    }
}
