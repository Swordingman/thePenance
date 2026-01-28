/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathConstraintData
/*     */   extends ConstraintData
/*     */ {
/*  38 */   final Array<BoneData> bones = new Array();
/*     */   
/*     */   SlotData target;
/*     */   
/*     */   PositionMode positionMode;
/*     */   SpacingMode spacingMode;
/*     */   RotateMode rotateMode;
/*     */   
/*     */   public PathConstraintData(String name) {
/*  47 */     super(name);
/*     */   }
/*     */   float offsetRotation; float position; float spacing; float rotateMix; float translateMix;
/*     */   
/*     */   public Array<BoneData> getBones() {
/*  52 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public SlotData getTarget() {
/*  57 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(SlotData target) {
/*  61 */     if (target == null) throw new IllegalArgumentException("target cannot be null."); 
/*  62 */     this.target = target;
/*     */   }
/*     */ 
/*     */   
/*     */   public PositionMode getPositionMode() {
/*  67 */     return this.positionMode;
/*     */   }
/*     */   
/*     */   public void setPositionMode(PositionMode positionMode) {
/*  71 */     if (positionMode == null) throw new IllegalArgumentException("positionMode cannot be null."); 
/*  72 */     this.positionMode = positionMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpacingMode getSpacingMode() {
/*  77 */     return this.spacingMode;
/*     */   }
/*     */   
/*     */   public void setSpacingMode(SpacingMode spacingMode) {
/*  81 */     if (spacingMode == null) throw new IllegalArgumentException("spacingMode cannot be null."); 
/*  82 */     this.spacingMode = spacingMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public RotateMode getRotateMode() {
/*  87 */     return this.rotateMode;
/*     */   }
/*     */   
/*     */   public void setRotateMode(RotateMode rotateMode) {
/*  91 */     if (rotateMode == null) throw new IllegalArgumentException("rotateMode cannot be null."); 
/*  92 */     this.rotateMode = rotateMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getOffsetRotation() {
/*  97 */     return this.offsetRotation;
/*     */   }
/*     */   
/*     */   public void setOffsetRotation(float offsetRotation) {
/* 101 */     this.offsetRotation = offsetRotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getPosition() {
/* 106 */     return this.position;
/*     */   }
/*     */   
/*     */   public void setPosition(float position) {
/* 110 */     this.position = position;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSpacing() {
/* 115 */     return this.spacing;
/*     */   }
/*     */   
/*     */   public void setSpacing(float spacing) {
/* 119 */     this.spacing = spacing;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotateMix() {
/* 124 */     return this.rotateMix;
/*     */   }
/*     */   
/*     */   public void setRotateMix(float rotateMix) {
/* 128 */     this.rotateMix = rotateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTranslateMix() {
/* 133 */     return this.translateMix;
/*     */   }
/*     */   
/*     */   public void setTranslateMix(float translateMix) {
/* 137 */     this.translateMix = translateMix;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum PositionMode
/*     */   {
/* 144 */     fixed, percent;
/*     */     
/* 146 */     public static final PositionMode[] values = values();
/*     */     
/*     */     static {
/*     */     
/*     */     }
/*     */   }
/*     */   
/* 153 */   public enum SpacingMode { length, fixed, percent;
/*     */     
/* 155 */     public static final SpacingMode[] values = values();
/*     */     
/*     */     static {
/*     */     
/*     */     } }
/*     */   
/*     */   public enum RotateMode {
/* 162 */     tangent, chain, chainScale;
/*     */     
/* 164 */     public static final RotateMode[] values = values();
/*     */     
/*     */     static {
/*     */     
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\PathConstraintData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */