package gamelogic.agent.tasks.intruder;

import datastructures.Vector2D;
import datastructures.minheap.Heap;
import datastructures.minheap.HeapItemInterface;
import gamelogic.agent.AStar;
import gamelogic.agent.tasks.TaskContainer.TaskType;
import gamelogic.agent.tasks.TaskInterface;
import gamelogic.controller.MovementController;
import gamelogic.controller.VisionController;
import gamelogic.datacarriers.Sound;
import gamelogic.datacarriers.VisionMemory;
import gamelogic.maps.graph.ExplorationGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class EvasionTaskBaseline implements TaskInterface {

    private ExplorationGraph graph;
    private TaskType type = TaskType.INTRUDER_EVASION;
    private Stack<Integer> futureMoves;
    private double targetAngle;
    private VisionMemory visionToEvadeFrom;
    private Sound soundToEvadeFrom;
    private boolean finished = false;

    @Override
    public int performTask(ExplorationGraph graph, double orientation, double pheromoneMarkerDirection, List<Sound> sounds, VisionMemory[] guardsSeen, VisionMemory[] intrudersSeen) {
        if (futureMoves == null || futureMoves.isEmpty()) {
            this.graph = graph;
            futureMoves = new Stack<>();

            double angle = targetAngle - 180.0;
            Vector2D goal = findGoal(angle);
            LinkedList<Vector2D> nodesToGoal = AStar.calculate(graph, graph.getCurrentPosition(), graph.getNode(goal), 3);

            this.futureMoves = MovementController.convertPath(graph, orientation, nodesToGoal, false);
        }
        if (futureMoves.size()==1) finished=true;
        return futureMoves.pop();
    }

    private Vector2D findGoal(double angle) {
        angle = checkAngle(angle);
        int threshold = 90;
        Heap<AngleItem> possibleAngles = new Heap<>(180);
        for (int i=0; i<=threshold; i=i+5) {
            AngleItem[] items = calculateAngleItems(angle, (double)i);
            possibleAngles.add(items[0]);
            possibleAngles.add(items[1]);
        }
        AngleItem bestAngle = possibleAngles.removeFirst();
        return VisionController.calculatePoint(graph.getCurrentPosition().COORDINATES, bestAngle.distance, bestAngle.angle);
    }

    private AngleItem[] calculateAngleItems(double angle, double i) {
        angle = checkAngle(angle);
        double angle1 = angle + i;
        double angle2 = angle - i;
        angle1 = checkAngle(angle1);
        angle2 = checkAngle(angle2);
        AngleItem item1 = new AngleItem(angle1, Math.abs(angle-i), findMaxDistance(angle1));
        AngleItem item2 = new AngleItem(angle2, Math.abs(angle-i), findMaxDistance(angle2));
        
        return new AngleItem[] {item1,item2};
    }

    private double findMaxDistance(double angle) {
        double distance = 10.0;
        Vector2D curPos = graph.getCurrentPosition().COORDINATES;
        Vector2D point = VisionController.calculatePoint(curPos, distance, angle);
        while(true) {
            if (graph.isVisited(point)) return distance;
            if (distance<0) {
                try {
                    throw new Exception("Don't know own position");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            distance--;
            point = VisionController.calculatePoint(curPos, distance, angle);
        }
    }

    private double checkAngle(double angle) {
        if (angle<0) {angle = angle+360.0;
        } else if (angle>360) { angle = angle - 360.0; }
        return angle;
    }

    public double getTargetAngle() { return targetAngle; }
    public Sound getSoundToEvadeFrom() { return soundToEvadeFrom; }
    public VisionMemory getVisionToEvadeFrom() { return visionToEvadeFrom; }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public TaskInterface newInstance() {
        return new EvasionTaskBaseline();
    }

    @Override
    public void setTarget(double target, VisionMemory visionMemory) {
        this.targetAngle = target;
        this.visionToEvadeFrom = visionMemory;
    }

    @Override
    public void setTarget(double target, Sound sound) {
        this.targetAngle = target;
        this.soundToEvadeFrom = sound;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            return ((EvasionTaskBaseline) other).getTarget().equals(this.targetAngle);
        }
        return false;
    }

    class AngleItem implements HeapItemInterface<AngleItem> {

        public final double angle;
        public final double difference;
        public final double distance;
        private int index;

        public AngleItem (double angle, double difference, double distance) {
            this.angle = angle;
            this.difference = difference;
            this.distance = distance;
        }

        @Override
        public void setHeapIndex(int index) {
            this.index = index;
        }

        @Override
        public int getHeapIndex() {
            return index;
        }

        @Override
        public int compareTo(AngleItem o) {
            if (this.distance < o.distance) {
                return -1;
            } else if (this.distance == o.distance) {
                if (this.difference < o.difference) {
                    return 1;
                } else if (this.difference > o.difference) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                return 1;
            }
        }

    }
}
