package com.github.alexthe666.alexsmobs.client.model;

import com.github.alexthe666.alexsmobs.entity.EntityShoebill;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;

public class ModelShoebill extends AdvancedEntityModel<EntityShoebill> {
   private final AdvancedModelBox root;
   private final AdvancedModelBox body;
   private final AdvancedModelBox tail;
   private final AdvancedModelBox leftWing;
   private final AdvancedModelBox leftWingFeathers;
   private final AdvancedModelBox rightWing;
   private final AdvancedModelBox rightWingFeathers;
   private final AdvancedModelBox leftLeg;
   private final AdvancedModelBox leftFoot;
   private final AdvancedModelBox rightLeg;
   private final AdvancedModelBox rightFoot;
   private final AdvancedModelBox headPivot;
   private final AdvancedModelBox head;
   private final AdvancedModelBox backHair;
   private final AdvancedModelBox hair_r1;
   private final AdvancedModelBox beak;
   private final AdvancedModelBox jaw;
   private final AdvancedModelBox jaw_r1;
   private final ModelAnimator animator;

   public ModelShoebill() {
      this.texWidth = 128;
      this.texHeight = 128;
      this.root = new AdvancedModelBox(this);
      this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.body = new AdvancedModelBox(this);
      this.body.setRotationPoint(0.0F, -17.0F, 2.0F);
      this.root.addChild(this.body);
      this.setRotationAngle(this.body, -0.9599F, 0.0F, 0.0F);
      this.body.setTextureOffset(0, 0).addBox(-4.0F, -3.0F, -7.0F, 8.0F, 6.0F, 13.0F, 0.0F, false);
      this.tail = new AdvancedModelBox(this);
      this.tail.setRotationPoint(0.0F, -2.0F, 6.0F);
      this.body.addChild(this.tail);
      this.tail.setTextureOffset(42, 27).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 2.0F, 9.0F, 0.0F, false);
      this.leftWing = new AdvancedModelBox(this);
      this.leftWing.setRotationPoint(4.0F, 0.3F, -3.4F);
      this.body.addChild(this.leftWing);
      this.leftWing.setTextureOffset(0, 20).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 6.0F, 13.0F, 0.0F, false);
      this.leftWingFeathers = new AdvancedModelBox(this);
      this.leftWingFeathers.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.leftWing.addChild(this.leftWingFeathers);
      this.leftWingFeathers.setTextureOffset(31, 8).addBox(0.2F, -4.0F, -3.0F, 0.0F, 6.0F, 12.0F, 0.0F, false);
      this.rightWing = new AdvancedModelBox(this);
      this.rightWing.setRotationPoint(-4.0F, 0.3F, -3.4F);
      this.body.addChild(this.rightWing);
      this.rightWing.setTextureOffset(0, 20).addBox(-1.0F, -3.0F, -2.0F, 2.0F, 6.0F, 13.0F, 0.0F, true);
      this.rightWingFeathers = new AdvancedModelBox(this);
      this.rightWingFeathers.setRotationPoint(0.0F, 0.0F, 9.0F);
      this.rightWing.addChild(this.rightWingFeathers);
      this.rightWingFeathers.setTextureOffset(31, 8).addBox(-0.2F, -4.0F, -3.0F, 0.0F, 6.0F, 12.0F, 0.0F, true);
      this.leftLeg = new AdvancedModelBox(this);
      this.leftLeg.setRotationPoint(2.5F, 3.0F, 4.0F);
      this.body.addChild(this.leftLeg);
      this.setRotationAngle(this.leftLeg, 0.9599F, 0.0F, 0.0F);
      this.leftLeg.setTextureOffset(18, 20).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, false);
      this.leftFoot = new AdvancedModelBox(this);
      this.leftFoot.setRotationPoint(0.0F, 12.0F, 0.0F);
      this.leftLeg.addChild(this.leftFoot);
      this.leftFoot.setTextureOffset(30, 0).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 0.0F, 5.0F, 0.0F, false);
      this.rightLeg = new AdvancedModelBox(this);
      this.rightLeg.setRotationPoint(-2.5F, 3.0F, 4.0F);
      this.body.addChild(this.rightLeg);
      this.setRotationAngle(this.rightLeg, 0.9599F, 0.0F, 0.0F);
      this.rightLeg.setTextureOffset(18, 20).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 12.0F, 0.0F, 0.0F, true);
      this.rightFoot = new AdvancedModelBox(this);
      this.rightFoot.setRotationPoint(0.0F, 12.0F, 0.0F);
      this.rightLeg.addChild(this.rightFoot);
      this.rightFoot.setTextureOffset(30, 0).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 0.0F, 5.0F, 0.0F, true);
      this.headPivot = new AdvancedModelBox(this);
      this.headPivot.setRotationPoint(0.0F, 1.0F, -4.0F);
      this.setRotationAngle(this.headPivot, -0.6109F, 0.0F, 0.0F);
      this.body.addChild(this.headPivot);
      this.head = new AdvancedModelBox(this);
      this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.headPivot.addChild(this.head);
      this.head.setTextureOffset(20, 29).addBox(-2.5F, -3.0F, -11.0F, 5.0F, 5.0F, 11.0F, 0.0F, false);
      this.head.setTextureOffset(34, 47).addBox(-2.5F, 2.0F, -11.0F, 5.0F, 1.0F, 1.0F, 0.0F, false);
      this.head.setTextureOffset(0, 0).addBox(0.0F, -6.0F, -12.0F, 0.0F, 6.0F, 5.0F, 0.0F, false);
      this.backHair = new AdvancedModelBox(this);
      this.backHair.setRotationPoint(0.0F, -3.0F, -10.0F);
      this.head.addChild(this.backHair);
      this.hair_r1 = new AdvancedModelBox(this);
      this.hair_r1.setRotationPoint(0.0F, 0.0F, -1.0F);
      this.backHair.addChild(this.hair_r1);
      this.setRotationAngle(this.hair_r1, -0.5236F, 0.0F, 0.0F);
      this.hair_r1.setTextureOffset(30, 6).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 5.0F, 0.0F, 0.0F, false);
      this.beak = new AdvancedModelBox(this);
      this.beak.setRotationPoint(0.0F, 2.0F, -9.0F);
      this.head.addChild(this.beak);
      this.setRotationAngle(this.beak, 0.3927F, 0.0F, 0.0F);
      this.beak.setTextureOffset(0, 40).addBox(-3.0F, -1.0F, 0.0F, 6.0F, 8.0F, 3.0F, 0.0F, false);
      this.beak.setTextureOffset(6, 0).addBox(-1.0F, 7.0F, 3.0F, 2.0F, 0.0F, 1.0F, 0.0F, false);
      this.jaw = new AdvancedModelBox(this);
      this.jaw.setRotationPoint(0.0F, 1.0F, 3.0F);
      this.beak.addChild(this.jaw);
      this.jaw_r1 = new AdvancedModelBox(this);
      this.jaw_r1.setRotationPoint(0.0F, 0.0F, 0.3F);
      this.jaw.addChild(this.jaw_r1);
      this.setRotationAngle(this.jaw_r1, -0.1745F, 0.0F, 0.0F);
      this.jaw_r1.setTextureOffset(0, 20).addBox(-2.5F, -1.4F, -0.3F, 5.0F, 7.0F, 1.0F, -0.03F, false);
      this.updateDefaultPose();
      this.animator = new ModelAnimator();
   }

   public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
      this.resetToDefaultPose();
      this.animator.update(entity);
      this.animator.setAnimation(EntityShoebill.ANIMATION_FISH);
      this.animator.startKeyframe(15);
      this.animator.rotate(this.head, Maths.rad(-40.0), 0.0F, 0.0F);
      this.animator.move(this.head, 0.0F, 0.5F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(5);
      this.animator.rotate(this.body, Maths.rad(40.0), 0.0F, 0.0F);
      this.animator.rotate(this.leftLeg, Maths.rad(-40.0), 0.0F, 0.0F);
      this.animator.rotate(this.rightLeg, Maths.rad(-40.0), 0.0F, 0.0F);
      this.animator.rotate(this.head, Maths.rad(80.0), 0.0F, 0.0F);
      this.animator.rotate(this.jaw, Maths.rad(20.0), 0.0F, 0.0F);
      this.animator.move(this.body, 0.0F, 1.0F, 0.0F);
      this.animator.move(this.head, 0.0F, 0.0F, -2.0F);
      this.animator.endKeyframe();
      this.animator.setStaticKeyframe(3);
      this.animator.resetKeyframe(5);
      this.animator.setAnimation(EntityShoebill.ANIMATION_BEAKSHAKE);
      this.animator.startKeyframe(4);
      this.animator.rotate(this.head, Maths.rad(40.0), Maths.rad(40.0), 0.0F);
      this.animator.move(this.head, 0.0F, 0.5F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(4);
      this.animator.rotate(this.head, Maths.rad(40.0), Maths.rad(-40.0), 0.0F);
      this.animator.move(this.head, 0.0F, 0.5F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(4);
      this.animator.rotate(this.head, Maths.rad(40.0), Maths.rad(40.0), 0.0F);
      this.animator.move(this.head, 0.0F, 0.5F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(4);
      this.animator.rotate(this.head, Maths.rad(40.0), Maths.rad(-40.0), 0.0F);
      this.animator.move(this.head, 0.0F, 0.5F, 0.0F);
      this.animator.endKeyframe();
      this.animator.resetKeyframe(4);
      this.animator.setAnimation(EntityShoebill.ANIMATION_ATTACK);
      this.animator.startKeyframe(5);
      this.animator.rotate(this.head, Maths.rad(-20.0), 0.0F, 0.0F);
      this.animator.rotate(this.jaw, Maths.rad(30.0), 0.0F, 0.0F);
      this.animator.move(this.head, 0.0F, 0.5F, 0.0F);
      this.animator.endKeyframe();
      this.animator.startKeyframe(5);
      this.animator.rotate(this.head, Maths.rad(60.0), 0.0F, 0.0F);
      this.animator.rotate(this.jaw, Maths.rad(5.0), 0.0F, 0.0F);
      this.animator.endKeyframe();
      this.animator.resetKeyframe(5);
   }

   public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
      AdvancedModelBox.rotateAngleX = x;
      AdvancedModelBox.rotateAngleY = y;
      AdvancedModelBox.rotateAngleZ = z;
   }

   public Iterable<BasicModelPart> parts() {
      return ImmutableList.of(this.root);
   }

   public Iterable<AdvancedModelBox> getAllParts() {
      return ImmutableList.of(
         this.root,
         this.body,
         this.leftLeg,
         this.rightLeg,
         this.leftWing,
         this.rightWing,
         this.tail,
         this.headPivot,
         this.head,
         this.beak,
         this.jaw,
         this.backHair,
         new AdvancedModelBox[]{this.leftFoot, this.rightFoot, this.hair_r1, this.jaw_r1, this.leftWingFeathers, this.rightWingFeathers}
      );
   }

   public void setupAnim(EntityShoebill entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      float walkSpeed = 0.7F;
      float walkDegree = 0.4F;
      float idleSpeed = 0.05F;
      float idleDegree = 0.2F;
      float flapSpeed = 0.4F;
      float flapDegree = 0.2F;
      float partialTick = Minecraft.m_91087_().m_91296_();
      float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
      float scaledLimbSwing = Math.min(1.0F, limbSwingAmount * 1.6F);
      float runProgress = Math.max(5.0F * scaledLimbSwing - flyProgress, 0.0F);
      this.progressRotationPrev(this.body, runProgress, Maths.rad(25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.rightLeg, runProgress, Maths.rad(-25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.leftLeg, runProgress, Maths.rad(-25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.head, runProgress, Maths.rad(-30.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.body, flyProgress, Maths.rad(35.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.rightLeg, flyProgress, Maths.rad(25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.leftLeg, flyProgress, Maths.rad(25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.rightFoot, flyProgress, Maths.rad(25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.leftFoot, flyProgress, Maths.rad(25.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.rightWing, flyProgress, Maths.rad(90.0), 0.0F, Maths.rad(-80.0), 5.0F);
      this.progressRotationPrev(this.leftWing, flyProgress, Maths.rad(90.0), 0.0F, Maths.rad(80.0), 5.0F);
      this.progressRotationPrev(this.head, flyProgress, Maths.rad(-20.0), 0.0F, 0.0F, 5.0F);
      this.progressRotationPrev(this.tail, flyProgress, Maths.rad(10.0), 0.0F, 0.0F, 5.0F);
      this.progressPositionPrev(this.rightLeg, flyProgress, 0.0F, -1.0F, 0.0F, 5.0F);
      this.progressPositionPrev(this.leftLeg, flyProgress, 0.0F, -1.0F, 0.0F, 5.0F);
      this.progressPositionPrev(this.body, flyProgress, 0.0F, 5.0F, 0.0F, 5.0F);
      this.progressPositionPrev(this.head, flyProgress, 0.0F, 1.5F, 0.0F, 5.0F);
      this.walk(this.head, -idleSpeed, idleDegree * 0.2F, false, 2.0F, 0.0F, ageInTicks, 1.0F);
      this.flap(this.tail, idleSpeed * 2.0F, idleDegree * 0.5F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
      if (flyProgress > 0.0F) {
         this.walk(this.rightLeg, walkSpeed, walkDegree * 0.2F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
         this.walk(this.leftLeg, walkSpeed, walkDegree * 0.2F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
         this.flap(this.rightWing, flapSpeed, flapDegree * 5.0F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
         this.flap(this.leftWing, flapSpeed, flapDegree * 5.0F, false, 0.0F, 0.0F, ageInTicks, 1.0F);
         this.walk(this.head, flapSpeed, flapDegree * 0.85F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
         this.bob(this.body, flapSpeed * 0.3F, flapDegree * 4.0F, true, ageInTicks, 1.0F);
      } else {
         this.walk(this.rightLeg, walkSpeed, walkDegree * 1.85F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
         this.walk(this.leftLeg, walkSpeed, walkDegree * 1.85F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
         this.walk(this.rightFoot, walkSpeed, walkDegree * 1.85F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
         this.walk(this.leftFoot, walkSpeed, walkDegree * 1.85F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
         this.walk(this.head, walkSpeed, walkDegree * 0.85F, true, 2.0F, 0.0F, limbSwing, limbSwingAmount);
         this.walk(this.tail, walkSpeed * 0.5F, walkDegree * 0.15F, true, -2.0F, 0.2F, limbSwing, limbSwingAmount);
      }

      this.head.rotateAngleZ += netHeadYaw * (float) (Math.PI / 180.0);
   }
}
