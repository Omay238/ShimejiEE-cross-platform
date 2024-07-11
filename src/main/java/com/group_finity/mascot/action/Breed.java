package com.group_finity.mascot.action;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.Tr;
import com.group_finity.mascot.animation.Animation;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;
import com.group_finity.mascot.script.VariableMap;

import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a new mascot
 * <p>
 * The mascot will be created after the animations/sub-actions have finished playing
 * */
public class Breed extends Animate {

    private static final Logger log = Logger.getLogger(Breed.class.getName());

    /**
     * @see Breed#getBornX()
     * */
    public static final String PARAMETER_BORNX = "BornX";
    private static final int DEFAULT_BORNX = 0;

    /**
     * @see Breed#getBornY()
     * */
    public static final String PARAMETER_BORNY = "BornY";
    private static final int DEFAULT_BORNY = 0;

    /**
     * @see Breed#getBornBehaviour()
     * */
    public static final String PARAMETER_BORNBEHAVIOUR = "BornBehaviour";
    private static final String DEFAULT_BORNBEHAVIOUR = "";

    /**
     * @see Breed#getBornMascot()
     */
    public static final String PARAMETER_BORNMASCOT = "BornMascot";
    private static final String DEFAULT_BORNMASCOT = null;

    /**
     * @see Breed#getBornTransient()
     */
    public static final String PARAMETER_BORNTRANSIENT = "BornTransient";
    private static final boolean DEFAULT_BORNTRANSIENT = false;

    public Breed(java.util.ResourceBundle schema, final List<Animation> animations, final VariableMap context) {
        super(schema, animations, context);
    }

    @Override
    protected void tick() throws LostGroundException, VariableException {
        super.tick();

        if (getTime() != getAnimation().getDuration() - 1) {
            return;
        }

        boolean allowed =
                getBornTransient()
                    ? getMascot().isTransientBreedingAllowed()
                    : getMascot().isBreedingAllowed();

        if (allowed) {
            breed();
        }
    }

    void breed() throws VariableException {
        String childType = getMascot().getImageSetNamed(getBornMascot()) != null ? getBornMascot() : getMascot().getImageSet();

        final Mascot newMascot = Mascot.createBlankFrom(getMascot());
        getMascot().setImageSet(childType);

        log.log(Level.INFO, "Breed Mascot ({0},{1},{2})", new Object[]{getMascot(), this, newMascot});

        if (getMascot().isLookRight()) {
            newMascot.setAnchor(new Point(
                    getMascot().getAnchor().x - getBornX(),
                    getMascot().getAnchor().y + getBornY()
            ));
        } else {
            newMascot.setAnchor(new Point(
                    getMascot().getAnchor().x + getBornX(),
                    getMascot().getAnchor().y + getBornY()
            ));
        }

        newMascot.setLookRight(getMascot().isLookRight());

        try {
            newMascot.setBehavior(newMascot.getOwnImageSet().getConfiguration().buildBehavior(getBornBehaviour()));
            getMascot().getManager().add(newMascot);

        } catch (final BehaviorInstantiationException | CantBeAliveException e) {
            log.log(Level.SEVERE, "Fatal Exception", e);
            newMascot.dispose();
            throw new VariableException(Tr.tr("FailedSetBehaviourErrorMessage"), e);
        }
    }

    /**
     * The X co-ordinates where the shimeji is spawned, this is relative to the shimeji creating it.
     * */
    private int getBornX() throws VariableException {
        int res = eval(getSchema().getString(PARAMETER_BORNX), Number.class, DEFAULT_BORNX).intValue();
        return (int) Math.round(res * getMascot().getScaling());
    }

    /**
     * The Y co-ordinates where the shimeji is spawned, this is relative to the shimeji creating it.
     * */
    private int getBornY() throws VariableException {
        int res = eval(getSchema().getString(PARAMETER_BORNY), Number.class, DEFAULT_BORNY).intValue();
        return (int) Math.round(res * getMascot().getScaling());
    }

    /**
     * The starting behaviour of the newly created mascot when it spawns
     * */
    private String getBornBehaviour() throws VariableException {
        return eval(getSchema().getString(PARAMETER_BORNBEHAVIOUR), String.class, DEFAULT_BORNBEHAVIOUR);
    }

    /**
     * Name of the imageSet that the new mascot will be of.
     * <p>
     * If this is not provided, the imageSet of the creating mascot will be used.
     */
    private String getBornMascot() throws VariableException {
        return eval(getSchema().getString(PARAMETER_BORNMASCOT), String.class, DEFAULT_BORNMASCOT);
    }

    /**
     * Whether the mascot will be considered 'transient'.
     * <p>
     * Unlike regular mascots, transient mascots will not be prevented from spawning when the user
     * disables regular breeding. Use this for short term mascots such as projectiles and particles.
     */
    private boolean getBornTransient() throws VariableException {
        return eval(getSchema().getString(PARAMETER_BORNTRANSIENT), Boolean.class, DEFAULT_BORNTRANSIENT);
    }

}
