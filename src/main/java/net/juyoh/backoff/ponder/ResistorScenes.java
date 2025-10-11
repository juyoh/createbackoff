package net.juyoh.backoff.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ResistorScenes {
    public static void intro(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.title("resistor_intro", Component.translatable("createbackoff.ponder.resistor_intro.header").toString());
        scene.configureBasePlate(0, 0, 4);
        scene.showBasePlate();

        BlockPos resistorPos = util.grid().at(2, 3, 1);
        Selection resistorSelection = util.select().position(resistorPos);
        Selection powerSelection = util.select().fromTo(0, 2, 1, 2, 2, 1);
        Selection all = util.select().everywhere();

        scene.addKeyframe();

        scene.idle(10);

        scene.world().showSection(resistorSelection, Direction.SOUTH);

        scene.idle(10);

        scene.overlay().showOutlineWithText(resistorSelection, 90)
                .text("The Resistor uses rotational force to repel entities")
                .pointAt(util.vector().blockSurface(resistorPos, Direction.WEST)
                        .add(-.5, .4, 0))
                .placeNearTarget();

        scene.idleSeconds(6);

        scene.addKeyframe();

        scene.world().showSection(powerSelection, Direction.EAST);

        scene.overlay().showText(60)
                .text("It is powered from below")
                .pointAt(util.vector().blockSurface(resistorPos, Direction.DOWN)
                        .add(-.5, .4, 0))
                .placeNearTarget();
        scene.world().setKineticSpeed(all, 16);
        scene.idle(10);
        scene.effects().indicateSuccess(resistorPos);

        int slowSize = 3;
        AABB sizeSlow = AABB.ofSize(resistorPos.getCenter().add(-0.5d, -0.5d, 0.5d), (slowSize * 2) - 2, (slowSize * 2) - 2, (slowSize * 2) - 2);

        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelection, sizeSlow, 240);

        scene.idleSeconds(4);
        ElementLink<ParrotElement> flappyBirb1 = scene.special().createBirb(util.vector().topOf(2, 6, 1), ParrotPose.FlappyPose::new);

        scene.overlay().showText(80)
                .text("When set up correctly, the Resistor can block entities")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().topOf(resistorPos.above(1)));

        scene.idle(2);
        scene.special().moveParrot(flappyBirb1, util.vector().of(0, -2.5, 0), 20);
        scene.idle(20);


        scene.idleSeconds(2);

        ElementLink<ParrotElement> flappyBirb2 = scene.special().createBirb(util.vector().topOf(0, 7, 2), ParrotPose.FlappyPose::new);
        ElementLink<ParrotElement> flappyBirb3 = scene.special().createBirb(util.vector().topOf(1, 6, 2), ParrotPose.FlappyPose::new);
        scene.special().rotateParrot(flappyBirb2, 0, 120, 0, 30);
        scene.special().rotateParrot(flappyBirb2, 0, -80, 0, 20);
        scene.special().moveParrot(flappyBirb2, util.vector().of(0, -2.5, 0), 30);
        scene.special().moveParrot(flappyBirb3, util.vector().of(0, -2.5, 0), 20);

        scene.idleSeconds(2);
        
        scene.addKeyframe();
        scene.markAsFinished();
        scene.setNextUpEnabled(true);
    }
    public static void speed(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.title("resistor_speed", Component.translatable("createbackoff.ponder.resistor_speed.header").toString());
        scene.setNextUpEnabled(true);

        scene.configureBasePlate(0, 0, 14);
        scene.showBasePlate();

        BlockPos resistorPos = util.grid().at(7, 3, 7);
        BlockPos speedometerPos = util.grid().at(5, 2, 7);
        BlockPos controllerPos = util.grid().at(3, 2, 7);

        Selection resistorSelection = util.select().position(resistorPos);
        Selection resistorSelectionSlow = util.select().position(resistorPos);
        Selection resistorSelectionFast = util.select().position(resistorPos);
        Selection controllerSelection = util.select().position(controllerPos);
        Selection powerSelection = util.select().fromTo(0, 2, 0, 14, 2, 14);
        Selection all = util.select().everywhere();
        Selection afterController = util.select().fromTo(7, 2, 7, 3, 2, 7);
        Selection beforeController = util.select().fromTo(3, 3, 7, 3, 3, 13);

        scene.addKeyframe();

        scene.idle(10);

        scene.world().showSection(resistorSelection, Direction.SOUTH);
        scene.world().setKineticSpeed(all, 0);

        scene.idle(10);

        scene.overlay().showOutlineWithText(resistorSelection, 170)
                .text("The bounding box's size is tied to the Resistor's speed")
                .pointAt(util.vector().blockSurface(resistorPos, Direction.WEST)
                        .add(-.5, .4, 0))
                .placeNearTarget();

        scene.addKeyframe();
        scene.idleSeconds(3);

        scene.world().showSection(powerSelection, Direction.EAST);
        scene.idle(10);
        scene.world().showSection(beforeController, Direction.NORTH);
        scene.idle(10);
        scene.world().setKineticSpeed(all, 16);
        scene.addKeyframe();

        int fastSize = 7;

        AABB sizeFast = AABB.ofSize(resistorPos.getCenter().add(-0.5d, -0.5d, -0.5d), (fastSize * 2) - 2, (fastSize * 2) - 2, (fastSize * 2) - 2);

        int slowSize = 3;
        AABB sizeSlow = AABB.ofSize(resistorPos.getCenter().add(-0.5d, -0.5d, -0.5d), (slowSize * 2) - 2, (slowSize * 2) - 2, (slowSize * 2) - 2);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelectionSlow, new AABB(resistorPos), 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelectionSlow, sizeSlow, 40);

        scene.idleSeconds(3);

        scene.overlay().showOutlineWithText(controllerSelection, 40)
                .text("Increasing the speed...")
                .pointAt(util.vector().blockSurface(controllerPos, Direction.NORTH)
                        .add(-1.2, 0.2, 0))
                .placeNearTarget();

        scene.idleSeconds(2);

        Vec3 inputVec = util.vector().of(3.5, 2.75 - 1 / 16f, 7);

        scene.overlay().showControls(inputVec, Pointing.UP, 40).rightClick();

        scene.overlay().showFilterSlotInput(inputVec, Direction.NORTH, 60);
        scene.world().setKineticSpeed(afterController, 32);

        scene.idle(10);

        scene.addKeyframe();

        scene.effects().rotationSpeedIndicator(speedometerPos);
        scene.effects().indicateSuccess(speedometerPos);

        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelectionFast, sizeSlow, 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelectionFast, sizeFast, 90);

        scene.idleSeconds(2);

        scene.overlay().showText(80)
                .text("Increases the size of it's influence")
                .pointAt(util.vector().blockSurface(resistorPos, Direction.UP)
                        .add(-.5, .4, 0))
                .placeNearTarget();

        scene.addKeyframe();

        scene.idleSeconds(4);

        scene.markAsFinished();

    }
    public static void filter(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.title("resistor_filter", Component.translatable("createbackoff.ponder.resistor_filter.header").toString());
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();

        BlockPos resistorPos = util.grid().at(2, 3, 3);
        BlockPos birbPos = util.grid().at(0, 7, 3);
        BlockPos itemPos = util.grid().at(1, 8, 2);
        Selection barrierSelection = util.select().fromTo(0, 4, 0, 6, 4, 6);
        Selection resistorSelection = util.select().position(resistorPos);
        Selection resistorSelectionSlow = util.select().position(resistorPos);

        Selection powerSelection = util.select().fromTo(0, 2, 3, 2, 2, 3);
        Selection all = util.select().everywhere();

        //scene.world().showSection(all, Direction.DOWN);

        scene.addKeyframe();

        scene.idle(10);

        scene.world().showSection(resistorSelection, Direction.SOUTH);
        scene.world().showSection(barrierSelection, Direction.DOWN);

        scene.idle(10);

        scene.overlay().showOutlineWithText(resistorSelection, 140)
                .text("By default, the Resistor will block everything except the Owner")
                .pointAt(util.vector().blockSurface(resistorPos, Direction.WEST)
                        .add(-.5, .4, 0))
                .placeNearTarget();

        scene.addKeyframe();

        scene.idleSeconds(3);

        scene.addKeyframe();

        scene.world().showSection(powerSelection, Direction.EAST);

        scene.world().setKineticSpeed(all, 16);
        scene.idle(10);
        scene.effects().indicateSuccess(resistorPos);

        int slowSize = 3;
        AABB sizeSlow = AABB.ofSize(resistorPos.getCenter().add(-0.5d, -0.5d, 0.5d), (slowSize * 2) - 2, (slowSize * 2) - 2, (slowSize * 2) - 2);

        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelectionSlow, new AABB(resistorPos), 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.RED, resistorSelectionSlow, sizeSlow, 240);

        scene.idleSeconds(2);

        scene.addKeyframe();

        ElementLink<ParrotElement> flappyBirb1 = scene.special().createBirb(util.vector().blockSurface(birbPos, Direction.UP), ParrotPose.FlappyPose::new);
        scene.special().moveParrot(flappyBirb1, util.vector().of(0, -2, 0), 20);
        ElementLink<EntityElement> itemElement = scene.world().createItemEntity(itemPos.getBottomCenter(), new Vec3(0, -0.2, 0), new ItemStack(Items.COBBLESTONE).copyWithCount(32));

        scene.idleSeconds(4);

        scene.special().hideElement(flappyBirb1, Direction.UP);
        scene.idle(10);
        scene.world().modifyEntities(ItemEntity.class, Entity::discard);

        scene.addKeyframe();
        scene.markAsFinished();
        scene.setNextUpEnabled(true);
    }
}