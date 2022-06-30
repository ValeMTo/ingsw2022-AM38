package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.view.SubPhaseEnum;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.controller.SpecialCardRequiredAction;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.specialCards.SpecialCardName;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * KeyboardInputReader class will be used as a separate thread that always waits for an input from the player
 * using the get methods of the view, it will decide the destin of the input, if it will be sent or not and
 * the connectionSocket method used.
 */
public class KeyboardInputReader implements Runnable {
    private final ViewState viewState;
    private final ConnectionSocket connectionSocket;
    private final ClientCLI clientCLI;
    private Scanner in;
    private String input;
    private Color pendingColor;


    /**
     * The constructor initializes the connectionSocket and viewState from the ClientCLI
     */
    public KeyboardInputReader(ConnectionSocket connectionSocket, ViewState viewState, ClientCLI clientCLI, Scanner in) {
        this.in = in;
        this.connectionSocket = connectionSocket;
        this.viewState = viewState;
        this.clientCLI = clientCLI;
    }

    /**
     * The run method, while the match is not ended, waits for inputs from the player, discarding the ones
     * acquired while the cli is not enabled
     */
    @Override
    public void run() {
        while (!viewState.isEndOfMatch()) {
            input = in.nextLine();
            if(input.equalsIgnoreCase("N"))
                viewState.refreshCLI();
            // We receive a generic input, then we choose the destin of  this input
            if (viewState.isActiveView()) {
                consumeInput();
            }
        }
    }

    /**
     * This method uses the input accordingly to the phase
     */
    private void consumeInput() {
        if(input.equalsIgnoreCase("END")&&viewState.isOptionalSpecialEffectUsage()&&viewState.getCurrentPhase().equals(PhaseEnum.SPECIAL_CARD_USAGE))
        {
            connectionSocket.sendTerminationSpecialCard();
        }
        else if(input.equalsIgnoreCase("V")||input.equalsIgnoreCase("S"))
            specialCardInputDecision();
        else if(viewState.getAcceptedUseSpecialCard())
        {
            specialCardActivation();
        }
        else if (viewState.getCurrentPhase().equals(PhaseEnum.PLANNING)) getFromInputCardPriorityAndSendMessage();
        else if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS)) {
            if (viewState.getSubPhaseEnum().equals(SubPhaseEnum.NO_SUB_PHASE) || (viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_COLOR)))
                fromInputSetColor();
            else if (viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_DINING_OR_ISLAND)) {
                fromInputSendIsDining();
            } else if (viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_ISLAND)) {
                fromInputSendIsland();
            }
        } else if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)) {
            fromInputSendIsland();
        } else if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_CHOOSE_CLOUD)) {
            fromInputSendCloud();
        }
        else if(viewState.getCurrentPhase().equals(PhaseEnum.SPECIAL_CARD_USAGE)){
            if(SpecialCardRequiredAction.isColorChoise(viewState.getSpecialPhase()))
                fromInputSetColor();
            else if(viewState.getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_ISLAND))
                fromInputSendIsland();
        }
    }

    /**
     * From the input String, get the number and after showing the chosen input, send the chosen card
     * If there is an error in the input just does not send anything.
     */
    private void getFromInputCardPriorityAndSendMessage() {
        int num;
        try {
            num = Integer.parseInt(input);
            System.out.println("You have chosen the card with " + num + " priority");
            if (!viewState.getUsableCards().contains(num))
                System.out.println(ClientCLI.CLIPink + "WRONG INPUT - you have choose a card that does not appear into the usable cards: " + viewState.getUsableCards() + ClientCLI.CLIEffectReset);
            else connectionSocket.setAssistantCard(num);
        } catch (InputMismatchException e) {
            System.out.println("Please, insert a number.");
        } catch (NumberFormatException exc) {
            System.out.println(ClientCLI.CLIPink + "WRONG INPUT! It is required a number, try again" + ClientCLI.CLIEffectReset);
        }
    }


    /**
     * Gets a pending color and decide how to send the color or store it
     */
    private void fromInputSetColor() {
        if (!input.equalsIgnoreCase("B") && !input.equalsIgnoreCase("G") && !input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("P") && !input.equalsIgnoreCase("R")) {
            System.out.println(ClientCLI.CLIPink + "Incorrect value, colors abbreviations are: " + ClientCLI.getAllColorAbbreviations() + ClientCLI.CLIEffectReset);
        } else {
            this.pendingColor = Color.toColor(input);
            // If it is required the color for a special card, is sent to the Server
            if (viewState.getCurrentPhase().equals(PhaseEnum.SPECIAL_CARD_USAGE) && viewState.getSpecialPhase() != null && SpecialCardRequiredAction.isColorChoise(viewState.getSpecialPhase())) {
                connectionSocket.chooseColor(this.pendingColor);
            }
            // If the current phase is the move students, then goes forward with the subPhase
            if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS))
                viewState.setSubPhase(SubPhaseEnum.CHOOSE_DINING_OR_ISLAND);
        }
    }

    /**
     * From the input, search if it is needed the DiningRoom or the Island, doing so changes the isDiningRoomFlag
     */
    private void fromInputSendIsDining() {
        if (input.equalsIgnoreCase("D")) {
            if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS)) {
                viewState.setSubPhase(SubPhaseEnum.NO_SUB_PHASE);
                connectionSocket.moveStudentToDiningRoom(pendingColor);
            }
        } else if (input.equalsIgnoreCase("I")) {
            viewState.setSubPhase(SubPhaseEnum.CHOOSE_ISLAND);
        } else
            System.out.println(ClientCLI.CLIPink + "Incorrect value, it is required D for DiningRoom or I for Island ( D / I )" + ClientCLI.CLIEffectReset);
    }

    /**
     * Gets the island position and send the message to Server for the move student or moveMothernature or for special card usage
     */
    private void fromInputSendIsland() {
        int position;
        try {
            position = Integer.parseInt(input);
            System.out.println("You have chosen the island with position " + position);
            if (!viewState.getUsableIslandPositions().contains(position))
                System.out.println(ClientCLI.CLIPink + "WRONG INPUT - you have choose an island that does not appear into the usable values, the islands are " + viewState.getUsableIslandPositions() + ClientCLI.CLIEffectReset);
            else if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS)) {
                viewState.setSubPhase(SubPhaseEnum.NO_SUB_PHASE);
                connectionSocket.moveStudentToIsland(pendingColor, position);
            } else if (viewState.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)) {
                connectionSocket.moveMotherNature(position);
            } else if (viewState.getCurrentPhase().equals(PhaseEnum.SPECIAL_CARD_USAGE) && viewState.getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_ISLAND)) {
                connectionSocket.chooseIsland(position);
            }
        } catch (InputMismatchException e) {
            System.out.println("Please, insert a number.");
        } catch (NumberFormatException exc) {
            System.out.println(ClientCLI.CLIPink + "WRONG INPUT! It is required a number, try again" + ClientCLI.CLIEffectReset);
        }
    }


    /**
     * From input send the chosen cloud
     */
    private void fromInputSendCloud() {
        int cloud;
        try {
            cloud = Integer.parseInt(input);
            System.out.println("You have chosen the cloud " + cloud);
            if (!viewState.getUsableClouds().contains(cloud))
                System.out.println(ClientCLI.CLIPink + "WRONG INPUT - you have choose a cloud that does not appear into the usable clouds: " + viewState.getUsableClouds() + ClientCLI.CLIEffectReset);
            else connectionSocket.chooseCloud(cloud);
        } catch (InputMismatchException e) {
            System.out.println("Please, insert a number.");
        } catch (NumberFormatException exc) {
            System.out.println(ClientCLI.CLIPink + "WRONG INPUT! It is required a number, try again" + ClientCLI.CLIEffectReset);
        }
    }

    /**
     * Print this message for the special card input usage
     */
    private void specialCardInputDecision() {
        if((!viewState.isExpert()||viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_COLOR)||viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_ISLAND)||viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_DINING_OR_ISLAND))) {
            viewState.setAcceptedUseSpecialCard(false);
            return;
        }
        if(input.equalsIgnoreCase("V")) {
            clientCLI.printSpecialCards();
            viewState.setAcceptedUseSpecialCard(false);
        }
        else if(input.equalsIgnoreCase("S")&&!viewState.getCurrentPhase().equals(PhaseEnum.PLANNING))
        {
            if(viewState.getSpecialCardUsage()) {
                System.out.println(ClientCLI.CLIPink + "Already used a special card this turn! wait for next turn to use them" + ClientCLI.CLIEffectReset);
                return;
            }
            clientCLI.specialCardUsage();
            viewState.setAcceptedUseSpecialCard(true);
        }
    }

    /**
     * Expect the decided special card and send the message
     */
    private void specialCardActivation(){
        try {
            if(input.equalsIgnoreCase("N")) {
                viewState.setAcceptedUseSpecialCard(false);
                viewState.refreshCLI();
                return;
            }
            for (String specialCardName : viewState.getUpperCaseSpecialCards())
            {
                if(specialCardName.equalsIgnoreCase(input)) {
                    connectionSocket.chooseSpecialCard(input);
                    viewState.setAcceptedUseSpecialCard(false);
                    return;
                }
            }
            System.out.println(ClientCLI.CLIPink + "Wrong input! No such special card name. Try again (ENTER 'N' to exit the choice)" + ClientCLI.CLIEffectReset);
            viewState.setAcceptedUseSpecialCard(false);
        }
        catch (FunctionNotImplementedException exc)
        {
            System.out.println(ClientCLI.CLIPink + "Not allowed to use special cards" + ClientCLI.CLIEffectReset);
        }
    }

    /**
     * @return the color already chosen
     */
    public Color getPendingColor() {
        return pendingColor;
    }

}
