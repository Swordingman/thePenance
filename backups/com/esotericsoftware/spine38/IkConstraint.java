/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.esotericsoftware.spine38.utils.SpineUtils;
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
/*     */ 
/*     */ public class IkConstraint
/*     */   implements Updatable
/*     */ {
/*     */   final IkConstraintData data;
/*     */   final Array<Bone> bones;
/*     */   Bone target;
/*     */   int bendDirection;
/*     */   boolean compress;
/*     */   boolean stretch;
/*  46 */   float mix = 1.0F;
/*     */   float softness;
/*     */   boolean active;
/*     */   
/*     */   public IkConstraint(IkConstraintData data, Skeleton skeleton) {
/*  51 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  52 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  53 */     this.data = data;
/*  54 */     this.mix = data.mix;
/*  55 */     this.softness = data.softness;
/*  56 */     this.bendDirection = data.bendDirection;
/*  57 */     this.compress = data.compress;
/*  58 */     this.stretch = data.stretch;
/*     */     
/*  60 */     this.bones = new Array(data.bones.size);
/*  61 */     for (BoneData boneData : data.bones)
/*  62 */       this.bones.add(skeleton.findBone(boneData.name)); 
/*  63 */     this.target = skeleton.findBone(data.target.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public IkConstraint(IkConstraint constraint, Skeleton skeleton) {
/*  68 */     if (constraint == null) throw new IllegalArgumentException("constraint cannot be null."); 
/*  69 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  70 */     this.data = constraint.data;
/*  71 */     this.bones = new Array(constraint.bones.size);
/*  72 */     for (Bone bone : constraint.bones)
/*  73 */       this.bones.add(skeleton.bones.get(bone.data.index)); 
/*  74 */     this.target = (Bone)skeleton.bones.get(constraint.target.data.index);
/*  75 */     this.mix = constraint.mix;
/*  76 */     this.softness = constraint.softness;
/*  77 */     this.bendDirection = constraint.bendDirection;
/*  78 */     this.compress = constraint.compress;
/*  79 */     this.stretch = constraint.stretch;
/*     */   }
/*     */ 
/*     */   
/*     */   public void apply() {
/*  84 */     update();
/*     */   }
/*     */   
/*     */   public void update() {
/*  88 */     Bone target = this.target;
/*  89 */     Array<Bone> bones = this.bones;
/*  90 */     switch (bones.size) {
/*     */       case 1:
/*  92 */         apply((Bone)bones.first(), target.worldX, target.worldY, this.compress, this.stretch, this.data.uniform, this.mix);
/*     */         break;
/*     */       case 2:
/*  95 */         apply((Bone)bones.first(), (Bone)bones.get(1), target.worldX, target.worldY, this.bendDirection, this.stretch, this.softness, this.mix);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Bone> getBones() {
/* 102 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public Bone getTarget() {
/* 107 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(Bone target) {
/* 111 */     if (target == null) throw new IllegalArgumentException("target cannot be null."); 
/* 112 */     this.target = target;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMix() {
/* 117 */     return this.mix;
/*     */   }
/*     */   
/*     */   public void setMix(float mix) {
/* 121 */     this.mix = mix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSoftness() {
/* 126 */     return this.softness;
/*     */   }
/*     */   
/*     */   public void setSoftness(float softness) {
/* 130 */     this.softness = softness;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBendDirection() {
/* 135 */     return this.bendDirection;
/*     */   }
/*     */   
/*     */   public void setBendDirection(int bendDirection) {
/* 139 */     this.bendDirection = bendDirection;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getCompress() {
/* 144 */     return this.compress;
/*     */   }
/*     */   
/*     */   public void setCompress(boolean compress) {
/* 148 */     this.compress = compress;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getStretch() {
/* 154 */     return this.stretch;
/*     */   }
/*     */   
/*     */   public void setStretch(boolean stretch) {
/* 158 */     this.stretch = stretch;
/*     */   }
/*     */   
/*     */   public boolean isActive() {
/* 162 */     return this.active;
/*     */   }
/*     */ 
/*     */   
/*     */   public IkConstraintData getData() {
/* 167 */     return this.data;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 171 */     return this.data.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void apply(Bone bone, float targetX, float targetY, boolean compress, boolean stretch, boolean uniform, float alpha) {
/*     */     float tx, ty, ps, x, y, d;
/* 177 */     if (bone == null) throw new IllegalArgumentException("bone cannot be null."); 
/* 178 */     if (!bone.appliedValid) bone.updateAppliedTransform(); 
/* 179 */     Bone p = bone.parent;
/* 180 */     float pa = p.a, pb = p.b, pc = p.c, pd = p.d;
/* 181 */     float rotationIK = -bone.ashearX - bone.arotation;
/* 182 */     switch (bone.data.transformMode) {
/*     */       case onlyTranslation:
/* 184 */         tx = targetX - bone.worldX;
/* 185 */         ty = targetY - bone.worldY;
/*     */         break;
/*     */       case noRotationOrReflection:
/* 188 */         rotationIK += SpineUtils.atan2(pc, pa) * 57.295776F;
/* 189 */         ps = Math.abs(pa * pd - pb * pc) / (pa * pa + pc * pc);
/* 190 */         pb = -pc * ps;
/* 191 */         pd = pa * ps;
/*     */       
/*     */       default:
/* 194 */         x = targetX - p.worldX; y = targetY - p.worldY;
/* 195 */         d = pa * pd - pb * pc;
/* 196 */         tx = (x * pd - y * pb) / d - bone.ax;
/* 197 */         ty = (y * pa - x * pc) / d - bone.ay; break;
/*     */     } 
/* 199 */     rotationIK += SpineUtils.atan2(ty, tx) * 57.295776F;
/* 200 */     if (bone.ascaleX < 0.0F) rotationIK += 180.0F; 
/* 201 */     if (rotationIK > 180.0F) {
/* 202 */       rotationIK -= 360.0F;
/* 203 */     } else if (rotationIK < -180.0F) {
/* 204 */       rotationIK += 360.0F;
/* 205 */     }  float sx = bone.ascaleX, sy = bone.ascaleY;
/* 206 */     if (compress || stretch) {
/* 207 */       switch (bone.data.transformMode) {
/*     */         case noScale:
/*     */         case noScaleOrReflection:
/* 210 */           tx = targetX - bone.worldX;
/* 211 */           ty = targetY - bone.worldY; break;
/*     */       } 
/* 213 */       float b = bone.data.length * sx, dd = (float)Math.sqrt((tx * tx + ty * ty));
/* 214 */       if ((compress && dd < b) || (stretch && dd > b && b > 1.0E-4F)) {
/* 215 */         float s = (dd / b - 1.0F) * alpha + 1.0F;
/* 216 */         sx *= s;
/* 217 */         if (uniform) sy *= s; 
/*     */       } 
/*     */     } 
/* 220 */     bone.updateWorldTransform(bone.ax, bone.ay, bone.arotation + rotationIK * alpha, sx, sy, bone.ashearX, bone.ashearY);
/*     */   }
/*     */   
/*     */   public static void apply(Bone parent, Bone child, float targetX, float targetY, int bendDir, boolean stretch, float softness, float alpha) {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: ifnonnull -> 14
/*     */     //   4: new java/lang/IllegalArgumentException
/*     */     //   7: dup
/*     */     //   8: ldc 'parent cannot be null.'
/*     */     //   10: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   13: athrow
/*     */     //   14: aload_1
/*     */     //   15: ifnonnull -> 28
/*     */     //   18: new java/lang/IllegalArgumentException
/*     */     //   21: dup
/*     */     //   22: ldc 'child cannot be null.'
/*     */     //   24: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   27: athrow
/*     */     //   28: fload #7
/*     */     //   30: fconst_0
/*     */     //   31: fcmpl
/*     */     //   32: ifne -> 40
/*     */     //   35: aload_1
/*     */     //   36: invokevirtual updateWorldTransform : ()V
/*     */     //   39: return
/*     */     //   40: aload_0
/*     */     //   41: getfield appliedValid : Z
/*     */     //   44: ifne -> 51
/*     */     //   47: aload_0
/*     */     //   48: invokevirtual updateAppliedTransform : ()V
/*     */     //   51: aload_1
/*     */     //   52: getfield appliedValid : Z
/*     */     //   55: ifne -> 62
/*     */     //   58: aload_1
/*     */     //   59: invokevirtual updateAppliedTransform : ()V
/*     */     //   62: aload_0
/*     */     //   63: getfield ax : F
/*     */     //   66: fstore #8
/*     */     //   68: aload_0
/*     */     //   69: getfield ay : F
/*     */     //   72: fstore #9
/*     */     //   74: aload_0
/*     */     //   75: getfield ascaleX : F
/*     */     //   78: fstore #10
/*     */     //   80: fload #10
/*     */     //   82: fstore #11
/*     */     //   84: aload_0
/*     */     //   85: getfield ascaleY : F
/*     */     //   88: fstore #12
/*     */     //   90: aload_1
/*     */     //   91: getfield ascaleX : F
/*     */     //   94: fstore #13
/*     */     //   96: fload #10
/*     */     //   98: fconst_0
/*     */     //   99: fcmpg
/*     */     //   100: ifge -> 119
/*     */     //   103: fload #10
/*     */     //   105: fneg
/*     */     //   106: fstore #10
/*     */     //   108: sipush #180
/*     */     //   111: istore #14
/*     */     //   113: iconst_m1
/*     */     //   114: istore #16
/*     */     //   116: goto -> 125
/*     */     //   119: iconst_0
/*     */     //   120: istore #14
/*     */     //   122: iconst_1
/*     */     //   123: istore #16
/*     */     //   125: fload #12
/*     */     //   127: fconst_0
/*     */     //   128: fcmpg
/*     */     //   129: ifge -> 142
/*     */     //   132: fload #12
/*     */     //   134: fneg
/*     */     //   135: fstore #12
/*     */     //   137: iload #16
/*     */     //   139: ineg
/*     */     //   140: istore #16
/*     */     //   142: fload #13
/*     */     //   144: fconst_0
/*     */     //   145: fcmpg
/*     */     //   146: ifge -> 162
/*     */     //   149: fload #13
/*     */     //   151: fneg
/*     */     //   152: fstore #13
/*     */     //   154: sipush #180
/*     */     //   157: istore #15
/*     */     //   159: goto -> 165
/*     */     //   162: iconst_0
/*     */     //   163: istore #15
/*     */     //   165: aload_1
/*     */     //   166: getfield ax : F
/*     */     //   169: fstore #17
/*     */     //   171: aload_0
/*     */     //   172: getfield a : F
/*     */     //   175: fstore #21
/*     */     //   177: aload_0
/*     */     //   178: getfield b : F
/*     */     //   181: fstore #22
/*     */     //   183: aload_0
/*     */     //   184: getfield c : F
/*     */     //   187: fstore #23
/*     */     //   189: aload_0
/*     */     //   190: getfield d : F
/*     */     //   193: fstore #24
/*     */     //   195: fload #10
/*     */     //   197: fload #12
/*     */     //   199: fsub
/*     */     //   200: invokestatic abs : (F)F
/*     */     //   203: ldc 1.0E-4
/*     */     //   205: fcmpg
/*     */     //   206: ifgt -> 213
/*     */     //   209: iconst_1
/*     */     //   210: goto -> 214
/*     */     //   213: iconst_0
/*     */     //   214: istore #25
/*     */     //   216: iload #25
/*     */     //   218: ifne -> 251
/*     */     //   221: fconst_0
/*     */     //   222: fstore #18
/*     */     //   224: fload #21
/*     */     //   226: fload #17
/*     */     //   228: fmul
/*     */     //   229: aload_0
/*     */     //   230: getfield worldX : F
/*     */     //   233: fadd
/*     */     //   234: fstore #19
/*     */     //   236: fload #23
/*     */     //   238: fload #17
/*     */     //   240: fmul
/*     */     //   241: aload_0
/*     */     //   242: getfield worldY : F
/*     */     //   245: fadd
/*     */     //   246: fstore #20
/*     */     //   248: goto -> 293
/*     */     //   251: aload_1
/*     */     //   252: getfield ay : F
/*     */     //   255: fstore #18
/*     */     //   257: fload #21
/*     */     //   259: fload #17
/*     */     //   261: fmul
/*     */     //   262: fload #22
/*     */     //   264: fload #18
/*     */     //   266: fmul
/*     */     //   267: fadd
/*     */     //   268: aload_0
/*     */     //   269: getfield worldX : F
/*     */     //   272: fadd
/*     */     //   273: fstore #19
/*     */     //   275: fload #23
/*     */     //   277: fload #17
/*     */     //   279: fmul
/*     */     //   280: fload #24
/*     */     //   282: fload #18
/*     */     //   284: fmul
/*     */     //   285: fadd
/*     */     //   286: aload_0
/*     */     //   287: getfield worldY : F
/*     */     //   290: fadd
/*     */     //   291: fstore #20
/*     */     //   293: aload_0
/*     */     //   294: getfield parent : Lcom/esotericsoftware/spine38/Bone;
/*     */     //   297: astore #26
/*     */     //   299: aload #26
/*     */     //   301: getfield a : F
/*     */     //   304: fstore #21
/*     */     //   306: aload #26
/*     */     //   308: getfield b : F
/*     */     //   311: fstore #22
/*     */     //   313: aload #26
/*     */     //   315: getfield c : F
/*     */     //   318: fstore #23
/*     */     //   320: aload #26
/*     */     //   322: getfield d : F
/*     */     //   325: fstore #24
/*     */     //   327: fconst_1
/*     */     //   328: fload #21
/*     */     //   330: fload #24
/*     */     //   332: fmul
/*     */     //   333: fload #22
/*     */     //   335: fload #23
/*     */     //   337: fmul
/*     */     //   338: fsub
/*     */     //   339: fdiv
/*     */     //   340: fstore #27
/*     */     //   342: fload #19
/*     */     //   344: aload #26
/*     */     //   346: getfield worldX : F
/*     */     //   349: fsub
/*     */     //   350: fstore #28
/*     */     //   352: fload #20
/*     */     //   354: aload #26
/*     */     //   356: getfield worldY : F
/*     */     //   359: fsub
/*     */     //   360: fstore #29
/*     */     //   362: fload #28
/*     */     //   364: fload #24
/*     */     //   366: fmul
/*     */     //   367: fload #29
/*     */     //   369: fload #22
/*     */     //   371: fmul
/*     */     //   372: fsub
/*     */     //   373: fload #27
/*     */     //   375: fmul
/*     */     //   376: fload #8
/*     */     //   378: fsub
/*     */     //   379: fstore #30
/*     */     //   381: fload #29
/*     */     //   383: fload #21
/*     */     //   385: fmul
/*     */     //   386: fload #28
/*     */     //   388: fload #23
/*     */     //   390: fmul
/*     */     //   391: fsub
/*     */     //   392: fload #27
/*     */     //   394: fmul
/*     */     //   395: fload #9
/*     */     //   397: fsub
/*     */     //   398: fstore #31
/*     */     //   400: fload #30
/*     */     //   402: fload #30
/*     */     //   404: fmul
/*     */     //   405: fload #31
/*     */     //   407: fload #31
/*     */     //   409: fmul
/*     */     //   410: fadd
/*     */     //   411: f2d
/*     */     //   412: invokestatic sqrt : (D)D
/*     */     //   415: d2f
/*     */     //   416: fstore #32
/*     */     //   418: aload_1
/*     */     //   419: getfield data : Lcom/esotericsoftware/spine38/BoneData;
/*     */     //   422: getfield length : F
/*     */     //   425: fload #13
/*     */     //   427: fmul
/*     */     //   428: fstore #33
/*     */     //   430: fload #32
/*     */     //   432: ldc 1.0E-4
/*     */     //   434: fcmpg
/*     */     //   435: ifge -> 476
/*     */     //   438: aload_0
/*     */     //   439: fload_2
/*     */     //   440: fload_3
/*     */     //   441: iconst_0
/*     */     //   442: iload #5
/*     */     //   444: iconst_0
/*     */     //   445: fload #7
/*     */     //   447: invokestatic apply : (Lcom/esotericsoftware/spine38/Bone;FFZZZF)V
/*     */     //   450: aload_1
/*     */     //   451: fload #17
/*     */     //   453: fload #18
/*     */     //   455: fconst_0
/*     */     //   456: aload_1
/*     */     //   457: getfield ascaleX : F
/*     */     //   460: aload_1
/*     */     //   461: getfield ascaleY : F
/*     */     //   464: aload_1
/*     */     //   465: getfield ashearX : F
/*     */     //   468: aload_1
/*     */     //   469: getfield ashearY : F
/*     */     //   472: invokevirtual updateWorldTransform : (FFFFFFF)V
/*     */     //   475: return
/*     */     //   476: fload_2
/*     */     //   477: aload #26
/*     */     //   479: getfield worldX : F
/*     */     //   482: fsub
/*     */     //   483: fstore #28
/*     */     //   485: fload_3
/*     */     //   486: aload #26
/*     */     //   488: getfield worldY : F
/*     */     //   491: fsub
/*     */     //   492: fstore #29
/*     */     //   494: fload #28
/*     */     //   496: fload #24
/*     */     //   498: fmul
/*     */     //   499: fload #29
/*     */     //   501: fload #22
/*     */     //   503: fmul
/*     */     //   504: fsub
/*     */     //   505: fload #27
/*     */     //   507: fmul
/*     */     //   508: fload #8
/*     */     //   510: fsub
/*     */     //   511: fstore #36
/*     */     //   513: fload #29
/*     */     //   515: fload #21
/*     */     //   517: fmul
/*     */     //   518: fload #28
/*     */     //   520: fload #23
/*     */     //   522: fmul
/*     */     //   523: fsub
/*     */     //   524: fload #27
/*     */     //   526: fmul
/*     */     //   527: fload #9
/*     */     //   529: fsub
/*     */     //   530: fstore #37
/*     */     //   532: fload #36
/*     */     //   534: fload #36
/*     */     //   536: fmul
/*     */     //   537: fload #37
/*     */     //   539: fload #37
/*     */     //   541: fmul
/*     */     //   542: fadd
/*     */     //   543: fstore #38
/*     */     //   545: fload #6
/*     */     //   547: fconst_0
/*     */     //   548: fcmpl
/*     */     //   549: ifeq -> 664
/*     */     //   552: fload #6
/*     */     //   554: fload #10
/*     */     //   556: fload #13
/*     */     //   558: fconst_1
/*     */     //   559: fadd
/*     */     //   560: fmul
/*     */     //   561: fconst_2
/*     */     //   562: fdiv
/*     */     //   563: fmul
/*     */     //   564: fstore #6
/*     */     //   566: fload #38
/*     */     //   568: f2d
/*     */     //   569: invokestatic sqrt : (D)D
/*     */     //   572: d2f
/*     */     //   573: fstore #39
/*     */     //   575: fload #39
/*     */     //   577: fload #32
/*     */     //   579: fsub
/*     */     //   580: fload #33
/*     */     //   582: fload #10
/*     */     //   584: fmul
/*     */     //   585: fsub
/*     */     //   586: fload #6
/*     */     //   588: fadd
/*     */     //   589: fstore #40
/*     */     //   591: fload #40
/*     */     //   593: fconst_0
/*     */     //   594: fcmpl
/*     */     //   595: ifle -> 664
/*     */     //   598: fconst_1
/*     */     //   599: fload #40
/*     */     //   601: fload #6
/*     */     //   603: fconst_2
/*     */     //   604: fmul
/*     */     //   605: fdiv
/*     */     //   606: invokestatic min : (FF)F
/*     */     //   609: fconst_1
/*     */     //   610: fsub
/*     */     //   611: fstore #41
/*     */     //   613: fload #40
/*     */     //   615: fload #6
/*     */     //   617: fconst_1
/*     */     //   618: fload #41
/*     */     //   620: fload #41
/*     */     //   622: fmul
/*     */     //   623: fsub
/*     */     //   624: fmul
/*     */     //   625: fsub
/*     */     //   626: fload #39
/*     */     //   628: fdiv
/*     */     //   629: fstore #41
/*     */     //   631: fload #36
/*     */     //   633: fload #41
/*     */     //   635: fload #36
/*     */     //   637: fmul
/*     */     //   638: fsub
/*     */     //   639: fstore #36
/*     */     //   641: fload #37
/*     */     //   643: fload #41
/*     */     //   645: fload #37
/*     */     //   647: fmul
/*     */     //   648: fsub
/*     */     //   649: fstore #37
/*     */     //   651: fload #36
/*     */     //   653: fload #36
/*     */     //   655: fmul
/*     */     //   656: fload #37
/*     */     //   658: fload #37
/*     */     //   660: fmul
/*     */     //   661: fadd
/*     */     //   662: fstore #38
/*     */     //   664: iload #25
/*     */     //   666: ifeq -> 818
/*     */     //   669: fload #33
/*     */     //   671: fload #10
/*     */     //   673: fmul
/*     */     //   674: fstore #33
/*     */     //   676: fload #38
/*     */     //   678: fload #32
/*     */     //   680: fload #32
/*     */     //   682: fmul
/*     */     //   683: fsub
/*     */     //   684: fload #33
/*     */     //   686: fload #33
/*     */     //   688: fmul
/*     */     //   689: fsub
/*     */     //   690: fconst_2
/*     */     //   691: fload #32
/*     */     //   693: fmul
/*     */     //   694: fload #33
/*     */     //   696: fmul
/*     */     //   697: fdiv
/*     */     //   698: fstore #39
/*     */     //   700: fload #39
/*     */     //   702: ldc -1.0
/*     */     //   704: fcmpg
/*     */     //   705: ifge -> 715
/*     */     //   708: ldc -1.0
/*     */     //   710: fstore #39
/*     */     //   712: goto -> 755
/*     */     //   715: fload #39
/*     */     //   717: fconst_1
/*     */     //   718: fcmpl
/*     */     //   719: ifle -> 755
/*     */     //   722: fconst_1
/*     */     //   723: fstore #39
/*     */     //   725: iload #5
/*     */     //   727: ifeq -> 755
/*     */     //   730: fload #11
/*     */     //   732: fload #38
/*     */     //   734: f2d
/*     */     //   735: invokestatic sqrt : (D)D
/*     */     //   738: d2f
/*     */     //   739: fload #32
/*     */     //   741: fload #33
/*     */     //   743: fadd
/*     */     //   744: fdiv
/*     */     //   745: fconst_1
/*     */     //   746: fsub
/*     */     //   747: fload #7
/*     */     //   749: fmul
/*     */     //   750: fconst_1
/*     */     //   751: fadd
/*     */     //   752: fmul
/*     */     //   753: fstore #11
/*     */     //   755: fload #39
/*     */     //   757: f2d
/*     */     //   758: invokestatic acos : (D)D
/*     */     //   761: d2f
/*     */     //   762: iload #4
/*     */     //   764: i2f
/*     */     //   765: fmul
/*     */     //   766: fstore #35
/*     */     //   768: fload #32
/*     */     //   770: fload #33
/*     */     //   772: fload #39
/*     */     //   774: fmul
/*     */     //   775: fadd
/*     */     //   776: fstore #21
/*     */     //   778: fload #33
/*     */     //   780: fload #35
/*     */     //   782: invokestatic sin : (F)F
/*     */     //   785: fmul
/*     */     //   786: fstore #22
/*     */     //   788: fload #37
/*     */     //   790: fload #21
/*     */     //   792: fmul
/*     */     //   793: fload #36
/*     */     //   795: fload #22
/*     */     //   797: fmul
/*     */     //   798: fsub
/*     */     //   799: fload #36
/*     */     //   801: fload #21
/*     */     //   803: fmul
/*     */     //   804: fload #37
/*     */     //   806: fload #22
/*     */     //   808: fmul
/*     */     //   809: fadd
/*     */     //   810: invokestatic atan2 : (FF)F
/*     */     //   813: fstore #34
/*     */     //   815: goto -> 1275
/*     */     //   818: fload #10
/*     */     //   820: fload #33
/*     */     //   822: fmul
/*     */     //   823: fstore #21
/*     */     //   825: fload #12
/*     */     //   827: fload #33
/*     */     //   829: fmul
/*     */     //   830: fstore #22
/*     */     //   832: fload #21
/*     */     //   834: fload #21
/*     */     //   836: fmul
/*     */     //   837: fstore #39
/*     */     //   839: fload #22
/*     */     //   841: fload #22
/*     */     //   843: fmul
/*     */     //   844: fstore #40
/*     */     //   846: fload #37
/*     */     //   848: fload #36
/*     */     //   850: invokestatic atan2 : (FF)F
/*     */     //   853: fstore #41
/*     */     //   855: fload #40
/*     */     //   857: fload #32
/*     */     //   859: fmul
/*     */     //   860: fload #32
/*     */     //   862: fmul
/*     */     //   863: fload #39
/*     */     //   865: fload #38
/*     */     //   867: fmul
/*     */     //   868: fadd
/*     */     //   869: fload #39
/*     */     //   871: fload #40
/*     */     //   873: fmul
/*     */     //   874: fsub
/*     */     //   875: fstore #23
/*     */     //   877: ldc -2.0
/*     */     //   879: fload #40
/*     */     //   881: fmul
/*     */     //   882: fload #32
/*     */     //   884: fmul
/*     */     //   885: fstore #42
/*     */     //   887: fload #40
/*     */     //   889: fload #39
/*     */     //   891: fsub
/*     */     //   892: fstore #43
/*     */     //   894: fload #42
/*     */     //   896: fload #42
/*     */     //   898: fmul
/*     */     //   899: ldc 4.0
/*     */     //   901: fload #43
/*     */     //   903: fmul
/*     */     //   904: fload #23
/*     */     //   906: fmul
/*     */     //   907: fsub
/*     */     //   908: fstore #24
/*     */     //   910: fload #24
/*     */     //   912: fconst_0
/*     */     //   913: fcmpl
/*     */     //   914: iflt -> 1048
/*     */     //   917: fload #24
/*     */     //   919: f2d
/*     */     //   920: invokestatic sqrt : (D)D
/*     */     //   923: d2f
/*     */     //   924: fstore #44
/*     */     //   926: fload #42
/*     */     //   928: fconst_0
/*     */     //   929: fcmpg
/*     */     //   930: ifge -> 938
/*     */     //   933: fload #44
/*     */     //   935: fneg
/*     */     //   936: fstore #44
/*     */     //   938: fload #42
/*     */     //   940: fload #44
/*     */     //   942: fadd
/*     */     //   943: fneg
/*     */     //   944: fconst_2
/*     */     //   945: fdiv
/*     */     //   946: fstore #44
/*     */     //   948: fload #44
/*     */     //   950: fload #43
/*     */     //   952: fdiv
/*     */     //   953: fstore #45
/*     */     //   955: fload #23
/*     */     //   957: fload #44
/*     */     //   959: fdiv
/*     */     //   960: fstore #46
/*     */     //   962: fload #45
/*     */     //   964: invokestatic abs : (F)F
/*     */     //   967: fload #46
/*     */     //   969: invokestatic abs : (F)F
/*     */     //   972: fcmpg
/*     */     //   973: ifge -> 981
/*     */     //   976: fload #45
/*     */     //   978: goto -> 983
/*     */     //   981: fload #46
/*     */     //   983: fstore #47
/*     */     //   985: fload #47
/*     */     //   987: fload #47
/*     */     //   989: fmul
/*     */     //   990: fload #38
/*     */     //   992: fcmpg
/*     */     //   993: ifgt -> 1048
/*     */     //   996: fload #38
/*     */     //   998: fload #47
/*     */     //   1000: fload #47
/*     */     //   1002: fmul
/*     */     //   1003: fsub
/*     */     //   1004: f2d
/*     */     //   1005: invokestatic sqrt : (D)D
/*     */     //   1008: d2f
/*     */     //   1009: iload #4
/*     */     //   1011: i2f
/*     */     //   1012: fmul
/*     */     //   1013: fstore #29
/*     */     //   1015: fload #41
/*     */     //   1017: fload #29
/*     */     //   1019: fload #47
/*     */     //   1021: invokestatic atan2 : (FF)F
/*     */     //   1024: fsub
/*     */     //   1025: fstore #34
/*     */     //   1027: fload #29
/*     */     //   1029: fload #12
/*     */     //   1031: fdiv
/*     */     //   1032: fload #47
/*     */     //   1034: fload #32
/*     */     //   1036: fsub
/*     */     //   1037: fload #10
/*     */     //   1039: fdiv
/*     */     //   1040: invokestatic atan2 : (FF)F
/*     */     //   1043: fstore #35
/*     */     //   1045: goto -> 1275
/*     */     //   1048: ldc 3.1415927
/*     */     //   1050: fstore #44
/*     */     //   1052: fload #32
/*     */     //   1054: fload #21
/*     */     //   1056: fsub
/*     */     //   1057: fstore #45
/*     */     //   1059: fload #45
/*     */     //   1061: fload #45
/*     */     //   1063: fmul
/*     */     //   1064: fstore #46
/*     */     //   1066: fconst_0
/*     */     //   1067: fstore #47
/*     */     //   1069: fconst_0
/*     */     //   1070: fstore #48
/*     */     //   1072: fload #32
/*     */     //   1074: fload #21
/*     */     //   1076: fadd
/*     */     //   1077: fstore #49
/*     */     //   1079: fload #49
/*     */     //   1081: fload #49
/*     */     //   1083: fmul
/*     */     //   1084: fstore #50
/*     */     //   1086: fconst_0
/*     */     //   1087: fstore #51
/*     */     //   1089: fload #21
/*     */     //   1091: fneg
/*     */     //   1092: fload #32
/*     */     //   1094: fmul
/*     */     //   1095: fload #39
/*     */     //   1097: fload #40
/*     */     //   1099: fsub
/*     */     //   1100: fdiv
/*     */     //   1101: fstore #23
/*     */     //   1103: fload #23
/*     */     //   1105: ldc -1.0
/*     */     //   1107: fcmpl
/*     */     //   1108: iflt -> 1211
/*     */     //   1111: fload #23
/*     */     //   1113: fconst_1
/*     */     //   1114: fcmpg
/*     */     //   1115: ifgt -> 1211
/*     */     //   1118: fload #23
/*     */     //   1120: f2d
/*     */     //   1121: invokestatic acos : (D)D
/*     */     //   1124: d2f
/*     */     //   1125: fstore #23
/*     */     //   1127: fload #21
/*     */     //   1129: fload #23
/*     */     //   1131: invokestatic cos : (F)F
/*     */     //   1134: fmul
/*     */     //   1135: fload #32
/*     */     //   1137: fadd
/*     */     //   1138: fstore #28
/*     */     //   1140: fload #22
/*     */     //   1142: fload #23
/*     */     //   1144: invokestatic sin : (F)F
/*     */     //   1147: fmul
/*     */     //   1148: fstore #29
/*     */     //   1150: fload #28
/*     */     //   1152: fload #28
/*     */     //   1154: fmul
/*     */     //   1155: fload #29
/*     */     //   1157: fload #29
/*     */     //   1159: fmul
/*     */     //   1160: fadd
/*     */     //   1161: fstore #24
/*     */     //   1163: fload #24
/*     */     //   1165: fload #46
/*     */     //   1167: fcmpg
/*     */     //   1168: ifge -> 1187
/*     */     //   1171: fload #23
/*     */     //   1173: fstore #44
/*     */     //   1175: fload #24
/*     */     //   1177: fstore #46
/*     */     //   1179: fload #28
/*     */     //   1181: fstore #45
/*     */     //   1183: fload #29
/*     */     //   1185: fstore #47
/*     */     //   1187: fload #24
/*     */     //   1189: fload #50
/*     */     //   1191: fcmpl
/*     */     //   1192: ifle -> 1211
/*     */     //   1195: fload #23
/*     */     //   1197: fstore #48
/*     */     //   1199: fload #24
/*     */     //   1201: fstore #50
/*     */     //   1203: fload #28
/*     */     //   1205: fstore #49
/*     */     //   1207: fload #29
/*     */     //   1209: fstore #51
/*     */     //   1211: fload #38
/*     */     //   1213: fload #46
/*     */     //   1215: fload #50
/*     */     //   1217: fadd
/*     */     //   1218: fconst_2
/*     */     //   1219: fdiv
/*     */     //   1220: fcmpg
/*     */     //   1221: ifgt -> 1251
/*     */     //   1224: fload #41
/*     */     //   1226: fload #47
/*     */     //   1228: iload #4
/*     */     //   1230: i2f
/*     */     //   1231: fmul
/*     */     //   1232: fload #45
/*     */     //   1234: invokestatic atan2 : (FF)F
/*     */     //   1237: fsub
/*     */     //   1238: fstore #34
/*     */     //   1240: fload #44
/*     */     //   1242: iload #4
/*     */     //   1244: i2f
/*     */     //   1245: fmul
/*     */     //   1246: fstore #35
/*     */     //   1248: goto -> 1275
/*     */     //   1251: fload #41
/*     */     //   1253: fload #51
/*     */     //   1255: iload #4
/*     */     //   1257: i2f
/*     */     //   1258: fmul
/*     */     //   1259: fload #49
/*     */     //   1261: invokestatic atan2 : (FF)F
/*     */     //   1264: fsub
/*     */     //   1265: fstore #34
/*     */     //   1267: fload #48
/*     */     //   1269: iload #4
/*     */     //   1271: i2f
/*     */     //   1272: fmul
/*     */     //   1273: fstore #35
/*     */     //   1275: fload #18
/*     */     //   1277: fload #17
/*     */     //   1279: invokestatic atan2 : (FF)F
/*     */     //   1282: iload #16
/*     */     //   1284: i2f
/*     */     //   1285: fmul
/*     */     //   1286: fstore #39
/*     */     //   1288: aload_0
/*     */     //   1289: getfield arotation : F
/*     */     //   1292: fstore #40
/*     */     //   1294: fload #34
/*     */     //   1296: fload #39
/*     */     //   1298: fsub
/*     */     //   1299: ldc 57.295776
/*     */     //   1301: fmul
/*     */     //   1302: iload #14
/*     */     //   1304: i2f
/*     */     //   1305: fadd
/*     */     //   1306: fload #40
/*     */     //   1308: fsub
/*     */     //   1309: fstore #34
/*     */     //   1311: fload #34
/*     */     //   1313: ldc 180.0
/*     */     //   1315: fcmpl
/*     */     //   1316: ifle -> 1329
/*     */     //   1319: fload #34
/*     */     //   1321: ldc 360.0
/*     */     //   1323: fsub
/*     */     //   1324: fstore #34
/*     */     //   1326: goto -> 1344
/*     */     //   1329: fload #34
/*     */     //   1331: ldc -180.0
/*     */     //   1333: fcmpg
/*     */     //   1334: ifge -> 1344
/*     */     //   1337: fload #34
/*     */     //   1339: ldc 360.0
/*     */     //   1341: fadd
/*     */     //   1342: fstore #34
/*     */     //   1344: aload_0
/*     */     //   1345: fload #8
/*     */     //   1347: fload #9
/*     */     //   1349: fload #40
/*     */     //   1351: fload #34
/*     */     //   1353: fload #7
/*     */     //   1355: fmul
/*     */     //   1356: fadd
/*     */     //   1357: fload #11
/*     */     //   1359: aload_0
/*     */     //   1360: getfield ascaleY : F
/*     */     //   1363: fconst_0
/*     */     //   1364: fconst_0
/*     */     //   1365: invokevirtual updateWorldTransform : (FFFFFFF)V
/*     */     //   1368: aload_1
/*     */     //   1369: getfield arotation : F
/*     */     //   1372: fstore #40
/*     */     //   1374: fload #35
/*     */     //   1376: fload #39
/*     */     //   1378: fadd
/*     */     //   1379: ldc 57.295776
/*     */     //   1381: fmul
/*     */     //   1382: aload_1
/*     */     //   1383: getfield ashearX : F
/*     */     //   1386: fsub
/*     */     //   1387: iload #16
/*     */     //   1389: i2f
/*     */     //   1390: fmul
/*     */     //   1391: iload #15
/*     */     //   1393: i2f
/*     */     //   1394: fadd
/*     */     //   1395: fload #40
/*     */     //   1397: fsub
/*     */     //   1398: fstore #35
/*     */     //   1400: fload #35
/*     */     //   1402: ldc 180.0
/*     */     //   1404: fcmpl
/*     */     //   1405: ifle -> 1418
/*     */     //   1408: fload #35
/*     */     //   1410: ldc 360.0
/*     */     //   1412: fsub
/*     */     //   1413: fstore #35
/*     */     //   1415: goto -> 1433
/*     */     //   1418: fload #35
/*     */     //   1420: ldc -180.0
/*     */     //   1422: fcmpg
/*     */     //   1423: ifge -> 1433
/*     */     //   1426: fload #35
/*     */     //   1428: ldc 360.0
/*     */     //   1430: fadd
/*     */     //   1431: fstore #35
/*     */     //   1433: aload_1
/*     */     //   1434: fload #17
/*     */     //   1436: fload #18
/*     */     //   1438: fload #40
/*     */     //   1440: fload #35
/*     */     //   1442: fload #7
/*     */     //   1444: fmul
/*     */     //   1445: fadd
/*     */     //   1446: aload_1
/*     */     //   1447: getfield ascaleX : F
/*     */     //   1450: aload_1
/*     */     //   1451: getfield ascaleY : F
/*     */     //   1454: aload_1
/*     */     //   1455: getfield ashearX : F
/*     */     //   1458: aload_1
/*     */     //   1459: getfield ashearY : F
/*     */     //   1462: invokevirtual updateWorldTransform : (FFFFFFF)V
/*     */     //   1465: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #227	-> 0
/*     */     //   #228	-> 14
/*     */     //   #229	-> 28
/*     */     //   #230	-> 35
/*     */     //   #231	-> 39
/*     */     //   #233	-> 40
/*     */     //   #234	-> 51
/*     */     //   #235	-> 62
/*     */     //   #237	-> 96
/*     */     //   #238	-> 103
/*     */     //   #239	-> 108
/*     */     //   #240	-> 113
/*     */     //   #242	-> 119
/*     */     //   #243	-> 122
/*     */     //   #245	-> 125
/*     */     //   #246	-> 132
/*     */     //   #247	-> 137
/*     */     //   #249	-> 142
/*     */     //   #250	-> 149
/*     */     //   #251	-> 154
/*     */     //   #253	-> 162
/*     */     //   #254	-> 165
/*     */     //   #255	-> 195
/*     */     //   #256	-> 216
/*     */     //   #257	-> 221
/*     */     //   #258	-> 224
/*     */     //   #259	-> 236
/*     */     //   #261	-> 251
/*     */     //   #262	-> 257
/*     */     //   #263	-> 275
/*     */     //   #265	-> 293
/*     */     //   #266	-> 299
/*     */     //   #267	-> 306
/*     */     //   #268	-> 313
/*     */     //   #269	-> 320
/*     */     //   #270	-> 327
/*     */     //   #271	-> 362
/*     */     //   #272	-> 400
/*     */     //   #273	-> 430
/*     */     //   #274	-> 438
/*     */     //   #275	-> 450
/*     */     //   #276	-> 475
/*     */     //   #278	-> 476
/*     */     //   #279	-> 485
/*     */     //   #280	-> 494
/*     */     //   #281	-> 532
/*     */     //   #282	-> 545
/*     */     //   #283	-> 552
/*     */     //   #284	-> 566
/*     */     //   #285	-> 591
/*     */     //   #286	-> 598
/*     */     //   #287	-> 613
/*     */     //   #288	-> 631
/*     */     //   #289	-> 641
/*     */     //   #290	-> 651
/*     */     //   #294	-> 664
/*     */     //   #295	-> 669
/*     */     //   #296	-> 676
/*     */     //   #297	-> 700
/*     */     //   #298	-> 708
/*     */     //   #299	-> 715
/*     */     //   #300	-> 722
/*     */     //   #301	-> 725
/*     */     //   #303	-> 755
/*     */     //   #304	-> 768
/*     */     //   #305	-> 778
/*     */     //   #306	-> 788
/*     */     //   #307	-> 815
/*     */     //   #308	-> 818
/*     */     //   #309	-> 825
/*     */     //   #310	-> 832
/*     */     //   #311	-> 855
/*     */     //   #312	-> 877
/*     */     //   #313	-> 894
/*     */     //   #314	-> 910
/*     */     //   #315	-> 917
/*     */     //   #316	-> 926
/*     */     //   #317	-> 938
/*     */     //   #318	-> 948
/*     */     //   #319	-> 962
/*     */     //   #320	-> 985
/*     */     //   #321	-> 996
/*     */     //   #322	-> 1015
/*     */     //   #323	-> 1027
/*     */     //   #324	-> 1045
/*     */     //   #327	-> 1048
/*     */     //   #328	-> 1069
/*     */     //   #329	-> 1089
/*     */     //   #330	-> 1103
/*     */     //   #331	-> 1118
/*     */     //   #332	-> 1127
/*     */     //   #333	-> 1140
/*     */     //   #334	-> 1150
/*     */     //   #335	-> 1163
/*     */     //   #336	-> 1171
/*     */     //   #337	-> 1175
/*     */     //   #338	-> 1179
/*     */     //   #339	-> 1183
/*     */     //   #341	-> 1187
/*     */     //   #342	-> 1195
/*     */     //   #343	-> 1199
/*     */     //   #344	-> 1203
/*     */     //   #345	-> 1207
/*     */     //   #348	-> 1211
/*     */     //   #349	-> 1224
/*     */     //   #350	-> 1240
/*     */     //   #352	-> 1251
/*     */     //   #353	-> 1267
/*     */     //   #356	-> 1275
/*     */     //   #357	-> 1288
/*     */     //   #358	-> 1294
/*     */     //   #359	-> 1311
/*     */     //   #360	-> 1319
/*     */     //   #361	-> 1329
/*     */     //   #362	-> 1344
/*     */     //   #363	-> 1368
/*     */     //   #364	-> 1374
/*     */     //   #365	-> 1400
/*     */     //   #366	-> 1408
/*     */     //   #367	-> 1418
/*     */     //   #368	-> 1433
/*     */     //   #369	-> 1465
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   113	6	14	os1	I
/*     */     //   116	3	16	s2	I
/*     */     //   159	3	15	os2	I
/*     */     //   224	27	18	cy	F
/*     */     //   236	15	19	cwx	F
/*     */     //   248	3	20	cwy	F
/*     */     //   613	51	41	p	F
/*     */     //   575	89	39	td	F
/*     */     //   591	73	40	sd	F
/*     */     //   700	115	39	cos	F
/*     */     //   815	3	34	a1	F
/*     */     //   768	50	35	a2	F
/*     */     //   1027	21	34	a1	F
/*     */     //   1045	3	35	a2	F
/*     */     //   926	122	44	q	F
/*     */     //   955	93	45	r0	F
/*     */     //   962	86	46	r1	F
/*     */     //   985	63	47	r	F
/*     */     //   1240	11	34	a1	F
/*     */     //   1248	3	35	a2	F
/*     */     //   839	436	39	aa	F
/*     */     //   846	429	40	bb	F
/*     */     //   855	420	41	ta	F
/*     */     //   887	388	42	c1	F
/*     */     //   894	381	43	c2	F
/*     */     //   1052	223	44	minAngle	F
/*     */     //   1059	216	45	minX	F
/*     */     //   1066	209	46	minDist	F
/*     */     //   1069	206	47	minY	F
/*     */     //   1072	203	48	maxAngle	F
/*     */     //   1079	196	49	maxX	F
/*     */     //   1086	189	50	maxDist	F
/*     */     //   1089	186	51	maxY	F
/*     */     //   0	1466	0	parent	Lcom/esotericsoftware/spine38/Bone;
/*     */     //   0	1466	1	child	Lcom/esotericsoftware/spine38/Bone;
/*     */     //   0	1466	2	targetX	F
/*     */     //   0	1466	3	targetY	F
/*     */     //   0	1466	4	bendDir	I
/*     */     //   0	1466	5	stretch	Z
/*     */     //   0	1466	6	softness	F
/*     */     //   0	1466	7	alpha	F
/*     */     //   68	1398	8	px	F
/*     */     //   74	1392	9	py	F
/*     */     //   80	1386	10	psx	F
/*     */     //   84	1382	11	sx	F
/*     */     //   90	1376	12	psy	F
/*     */     //   96	1370	13	csx	F
/*     */     //   122	1344	14	os1	I
/*     */     //   165	1301	15	os2	I
/*     */     //   125	1341	16	s2	I
/*     */     //   171	1295	17	cx	F
/*     */     //   257	1209	18	cy	F
/*     */     //   275	1191	19	cwx	F
/*     */     //   293	1173	20	cwy	F
/*     */     //   177	1289	21	a	F
/*     */     //   183	1283	22	b	F
/*     */     //   189	1277	23	c	F
/*     */     //   195	1271	24	d	F
/*     */     //   216	1250	25	u	Z
/*     */     //   299	1167	26	pp	Lcom/esotericsoftware/spine38/Bone;
/*     */     //   342	1124	27	id	F
/*     */     //   352	1114	28	x	F
/*     */     //   362	1104	29	y	F
/*     */     //   381	1085	30	dx	F
/*     */     //   400	1066	31	dy	F
/*     */     //   418	1048	32	l1	F
/*     */     //   430	1036	33	l2	F
/*     */     //   1267	199	34	a1	F
/*     */     //   1275	191	35	a2	F
/*     */     //   513	953	36	tx	F
/*     */     //   532	934	37	ty	F
/*     */     //   545	921	38	dd	F
/*     */     //   1288	178	39	os	F
/*     */     //   1294	172	40	rotation	F
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\IkConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */