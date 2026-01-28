/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
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
/*     */ public class BoneData
/*     */ {
/*     */   final int index;
/*     */   final String name;
/*     */   final BoneData parent;
/*     */   float length;
/*     */   float x;
/*     */   float y;
/*     */   float rotation;
/*  40 */   float scaleX = 1.0F; float scaleY = 1.0F; float shearX;
/*  41 */   TransformMode transformMode = TransformMode.normal;
/*     */   
/*     */   float shearY;
/*     */   boolean skinRequired;
/*  45 */   final Color color = new Color(0.61F, 0.61F, 0.61F, 1.0F);
/*     */ 
/*     */   
/*     */   public BoneData(int index, String name, BoneData parent) {
/*  49 */     if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  50 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/*  51 */     this.index = index;
/*  52 */     this.name = name;
/*  53 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BoneData(BoneData bone, BoneData parent) {
/*  59 */     if (bone == null) throw new IllegalArgumentException("bone cannot be null."); 
/*  60 */     this.index = bone.index;
/*  61 */     this.name = bone.name;
/*  62 */     this.parent = parent;
/*  63 */     this.length = bone.length;
/*  64 */     this.x = bone.x;
/*  65 */     this.y = bone.y;
/*  66 */     this.rotation = bone.rotation;
/*  67 */     this.scaleX = bone.scaleX;
/*  68 */     this.scaleY = bone.scaleY;
/*  69 */     this.shearX = bone.shearX;
/*  70 */     this.shearY = bone.shearY;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIndex() {
/*  75 */     return this.index;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  80 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public BoneData getParent() {
/*  85 */     return this.parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getLength() {
/*  90 */     return this.length;
/*     */   }
/*     */   
/*     */   public void setLength(float length) {
/*  94 */     this.length = length;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getX() {
/*  99 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/* 103 */     this.x = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getY() {
/* 108 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/* 112 */     this.y = y;
/*     */   }
/*     */   
/*     */   public void setPosition(float x, float y) {
/* 116 */     this.x = x;
/* 117 */     this.y = y;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotation() {
/* 122 */     return this.rotation;
/*     */   }
/*     */   
/*     */   public void setRotation(float rotation) {
/* 126 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleX() {
/* 131 */     return this.scaleX;
/*     */   }
/*     */   
/*     */   public void setScaleX(float scaleX) {
/* 135 */     this.scaleX = scaleX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleY() {
/* 140 */     return this.scaleY;
/*     */   }
/*     */   
/*     */   public void setScaleY(float scaleY) {
/* 144 */     this.scaleY = scaleY;
/*     */   }
/*     */   
/*     */   public void setScale(float scaleX, float scaleY) {
/* 148 */     this.scaleX = scaleX;
/* 149 */     this.scaleY = scaleY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getShearX() {
/* 154 */     return this.shearX;
/*     */   }
/*     */   
/*     */   public void setShearX(float shearX) {
/* 158 */     this.shearX = shearX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getShearY() {
/* 163 */     return this.shearY;
/*     */   }
/*     */   
/*     */   public void setShearY(float shearY) {
/* 167 */     this.shearY = shearY;
/*     */   }
/*     */ 
/*     */   
/*     */   public TransformMode getTransformMode() {
/* 172 */     return this.transformMode;
/*     */   }
/*     */   
/*     */   public void setTransformMode(TransformMode transformMode) {
/* 176 */     if (transformMode == null) throw new IllegalArgumentException("transformMode cannot be null."); 
/* 177 */     this.transformMode = transformMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getSkinRequired() {
/* 184 */     return this.skinRequired;
/*     */   }
/*     */   
/*     */   public void setSkinRequired(boolean skinRequired) {
/* 188 */     this.skinRequired = skinRequired;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Color getColor() {
/* 194 */     return this.color;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 198 */     return this.name;
/*     */   }
/*     */   
/*     */   public enum TransformMode
/*     */   {
/* 203 */     normal, onlyTranslation, noRotationOrReflection, noScale, noScaleOrReflection;
/*     */     
/* 205 */     public static final TransformMode[] values = values();
/*     */     
/*     */     static {
/*     */     
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\BoneData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */