/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */
package mods.railcraft.api.signals;

import mods.railcraft.api.core.WorldCoordinate;
import net.minecraft.tileentity.TileEntity;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class SignalController extends AbstractPair {
    public SignalController(String locTag, TileEntity tile, int maxPairings) {
        super(locTag, tile, maxPairings);
    }

    public SignalReceiver getReceiverAt(WorldCoordinate coord) {
        TileEntity recv = getPairAt(coord);
        if (recv != null) {
            return ((IReceiverTile) recv).getReceiver();
        }
        return null;
    }

    public abstract SignalAspect getAspectFor(WorldCoordinate receiver);

    public boolean sendAspectTo(WorldCoordinate receiver, SignalAspect aspect) {
        SignalReceiver recv = getReceiverAt(receiver);
        if (recv != null) {
            recv.onControllerAspectChange(this, aspect);
            return true;
        }
        return false;
    }

    @Override
    protected String getTagName() {
        return "controller";
    }

    @Override
    public boolean isValidPair(TileEntity otherTile) {
        return isValidPair(null, otherTile);
    }

    @Override
    public boolean isValidPair(WorldCoordinate otherCoord, TileEntity otherTile) {
        if (otherTile instanceof IReceiverTile) {
            SignalReceiver receiver = ((IReceiverTile) otherTile).getReceiver();
            return receiver.isPairedWith(getCoords());
        }
        return false;
    }

    @Deprecated
    public void registerLegacyReceiver(int x, int y, int z) {
        pairings.add(new WorldCoordinate(0, x, y, z));
    }

    public void registerReceiver(SignalReceiver receiver) {
        WorldCoordinate coords = receiver.getCoords();
        addPairing(coords);
        receiver.registerController(this);
        receiver.onControllerAspectChange(this, getAspectFor(coords));
    }

    @Override
    public void tickClient() {
        super.tickClient();
        if (SignalTools.effectManager != null && SignalTools.effectManager.isTuningAuraActive()) {
            for (WorldCoordinate coord : getPairs()) {
                SignalReceiver receiver = getReceiverAt(coord);
                if (receiver != null) {
                    SignalTools.effectManager.tuningEffect(getTile(), receiver.getTile());
                }
            }
        }
    }
}
