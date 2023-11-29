package no.ntnu.message;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorActuatorNode;

import java.util.Map;

public class RequestNodeInfoCommand extends Command {

    @Override
    public Message execute(ControlPanelLogic logic) {
        GreenhouseSimulator simulator = logic.getGreenhouseSimulator();
        Map<Integer, SensorActuatorNode> nodesInfo = simulator.getNodesInfo();

        return new NodeInfoMessage(nodesInfo);
    }
}