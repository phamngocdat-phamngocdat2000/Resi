package events;

import infrastructure.element.Element;
import infrastructure.entity.Node;
import infrastructure.event.EventController;
import infrastructure.state.Type;
import network.elements.EntranceBuffer;
import network.elements.ExitBuffer;
import network.elements.Packet;
import network.elements.UnidirectionalWay;
import network.entities.Host;
import network.states.unidirectionalway.W1;
import simulator.DiscreteEventSimulator;

public class GReachingDestinationEvent extends EventController {

	/**
	 * This is the constructor method of GReachingDestinationEvent class extending
	 * Event class. This is the event which represents type (G) event: packet
	 * reaches the destination node
	 * 
	 * @param sim
	 * @param startTime
	 * @param endTime
	 * @param elem
	 * @param p
	 */
	public GReachingDestinationEvent(DiscreteEventSimulator sim, long startTime, long endTime, Element elem, Packet p) {
		// countSubEvent++;
		super(sim, endTime);
		this.startTime = startTime;
		this.endTime = endTime;
		this.element = elem;
		this.packet = p;
	}

	@Override
	public void actions() {
		EntranceBuffer entranceBuffer = null;
		UnidirectionalWay unidirectionalWay = (UnidirectionalWay) element;
		ExitBuffer exitBuffer = unidirectionalWay.getFromNode().physicalLayer.exitBuffers
				.get(unidirectionalWay.getToNode().getId());
		Node nextNode = unidirectionalWay.getToNode();

		if (packet.getState().type == Type.P3 && unidirectionalWay.getState() instanceof W1
				&& nextNode.isDestinationNode() && unidirectionalWay.getPacket() == packet) {
			unidirectionalWay.removePacket();
			Host destinationNode = (Host) nextNode;
			destinationNode.receivePacket(packet);

			changeState(entranceBuffer, exitBuffer, unidirectionalWay);
		}

	}

	/**
	 * This method is used to change the state of exit buffer and unidirectional way
	 */
	@Override
	public void changeState(EntranceBuffer entranceBuffer, ExitBuffer exitBuffer, UnidirectionalWay unidirectionalWay) {
		packet.setType(Type.P6); // change state packet
		changeWayStateW0(unidirectionalWay); // change state of uniWay

		// change state of EXB
		if (exitBuffer.getState().type == Type.X00) {
			changeEXBStateX01(exitBuffer);
		}
		if (exitBuffer.getState().type == Type.X10) {
			changeEXBStateX11(exitBuffer);
		}
	}
}