/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.graphics.g2d.Batch;
/*     */ import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
/*     */ import com.badlogic.gdx.math.Vector2;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.NumberUtils;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.ClippingAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.RegionAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.SkeletonAttachment;
/*     */ import com.esotericsoftware.spine38.utils.SkeletonClipping;
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
/*     */ 
/*     */ public class SkeletonMeshRenderer
/*     */ {
/*  50 */   private static final short[] quadTriangles = new short[] { 0, 1, 2, 2, 3, 0 };
/*     */   
/*     */   private boolean premultipliedAlpha;
/*  53 */   private final FloatArray vertices = new FloatArray(32);
/*  54 */   private final SkeletonClipping clipper = new SkeletonClipping();
/*     */   private VertexEffect vertexEffect;
/*  56 */   private final Vector2 temp = new Vector2();
/*  57 */   private final Vector2 temp2 = new Vector2();
/*  58 */   private final Color temp3 = new Color();
/*  59 */   private final Color temp4 = new Color();
/*  60 */   private final Color temp5 = new Color();
/*  61 */   private final Color temp6 = new Color();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void draw(Batch batch, Skeleton skeleton) {
/*  69 */     if (batch instanceof PolygonSpriteBatch) {
/*  70 */       draw((PolygonSpriteBatch)batch, skeleton);
/*     */       return;
/*     */     } 
/*  73 */     if (batch == null) throw new IllegalArgumentException("batch cannot be null."); 
/*  74 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null.");
/*     */     
/*  76 */     VertexEffect vertexEffect = this.vertexEffect;
/*  77 */     if (vertexEffect != null) vertexEffect.begin(skeleton);
/*     */     
/*  79 */     boolean premultipliedAlpha = this.premultipliedAlpha;
/*  80 */     BlendMode blendMode = null;
/*  81 */     float[] vertices = this.vertices.items;
/*  82 */     Color skeletonColor = skeleton.color;
/*  83 */     float r = skeletonColor.r, g = skeletonColor.g, b = skeletonColor.b, a = skeletonColor.a;
/*  84 */     Array<Slot> drawOrder = skeleton.drawOrder;
/*  85 */     int i = 0, n = drawOrder.size; while (true) { Slot slot; if (i < n)
/*  86 */       { slot = (Slot)drawOrder.get(i);
/*  87 */         if (!slot.bone.active)
/*  88 */           continue;  Attachment attachment = slot.attachment;
/*  89 */         if (attachment instanceof RegionAttachment)
/*  90 */         { RegionAttachment region = (RegionAttachment)attachment;
/*  91 */           region.computeWorldVertices(slot.getBone(), vertices, 0, 5);
/*  92 */           Color color = region.getColor(), slotColor = slot.getColor();
/*  93 */           float alpha = a * slotColor.a * color.a * 255.0F;
/*  94 */           float multiplier = premultipliedAlpha ? alpha : 255.0F;
/*     */           
/*  96 */           BlendMode slotBlendMode = slot.data.getBlendMode();
/*  97 */           if (slotBlendMode != blendMode) {
/*  98 */             if (slotBlendMode == BlendMode.additive && premultipliedAlpha) {
/*  99 */               slotBlendMode = BlendMode.normal;
/* 100 */               alpha = 0.0F;
/*     */             } 
/* 102 */             blendMode = slotBlendMode;
/* 103 */             batch.setBlendFunction(blendMode.getSource(premultipliedAlpha), blendMode.getDest());
/*     */           } 
/*     */           
/* 106 */           float c = NumberUtils.intToFloatColor((int)alpha << 24 | (int)(b * slotColor.b * color.b * multiplier) << 16 | (int)(g * slotColor.g * color.g * multiplier) << 8 | (int)(r * slotColor.r * color.r * multiplier));
/*     */ 
/*     */ 
/*     */           
/* 110 */           float[] uvs = region.getUVs();
/* 111 */           for (int u = 0, v = 2; u < 8; u += 2, v += 5) {
/* 112 */             vertices[v] = c;
/* 113 */             vertices[v + 1] = uvs[u];
/* 114 */             vertices[v + 2] = uvs[u + 1];
/*     */           } 
/*     */           
/* 117 */           if (vertexEffect != null) applyVertexEffect(vertices, 20, 5, c, 0.0F);
/*     */           
/* 119 */           batch.draw(region.getRegion().getTexture(), vertices, 0, 20); }
/*     */         else
/* 121 */         { if (attachment instanceof ClippingAttachment)
/* 122 */           { this.clipper.clipStart(slot, (ClippingAttachment)attachment); }
/*     */           else
/*     */           
/* 125 */           { if (attachment instanceof com.esotericsoftware.spine38.attachments.MeshAttachment) {
/* 126 */               throw new RuntimeException(batch.getClass().getSimpleName() + " cannot render meshes, PolygonSpriteBatch or TwoColorPolygonBatch is required.");
/*     */             }
/*     */             
/* 129 */             if (attachment instanceof SkeletonAttachment) {
/* 130 */               Skeleton attachmentSkeleton = ((SkeletonAttachment)attachment).getSkeleton();
/* 131 */               if (attachmentSkeleton != null) draw(batch, attachmentSkeleton);
/*     */             
/*     */             } 
/* 134 */             this.clipper.clipEnd(slot); }  continue; }  } else { break; }  this.clipper.clipEnd(slot); i++; }
/*     */     
/* 136 */     this.clipper.clipEnd();
/* 137 */     if (vertexEffect != null) vertexEffect.end();
/*     */   
/*     */   }
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
/*     */   public void draw(PolygonSpriteBatch batch, Skeleton skeleton) {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ifnonnull -> 14
/*     */     //   4: new java/lang/IllegalArgumentException
/*     */     //   7: dup
/*     */     //   8: ldc 'batch cannot be null.'
/*     */     //   10: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   13: athrow
/*     */     //   14: aload_2
/*     */     //   15: ifnonnull -> 28
/*     */     //   18: new java/lang/IllegalArgumentException
/*     */     //   21: dup
/*     */     //   22: ldc 'skeleton cannot be null.'
/*     */     //   24: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   27: athrow
/*     */     //   28: aload_0
/*     */     //   29: getfield temp : Lcom/badlogic/gdx/math/Vector2;
/*     */     //   32: astore_3
/*     */     //   33: aload_0
/*     */     //   34: getfield temp2 : Lcom/badlogic/gdx/math/Vector2;
/*     */     //   37: astore #4
/*     */     //   39: aload_0
/*     */     //   40: getfield temp3 : Lcom/badlogic/gdx/graphics/Color;
/*     */     //   43: astore #5
/*     */     //   45: aload_0
/*     */     //   46: getfield temp4 : Lcom/badlogic/gdx/graphics/Color;
/*     */     //   49: astore #6
/*     */     //   51: aload_0
/*     */     //   52: getfield temp5 : Lcom/badlogic/gdx/graphics/Color;
/*     */     //   55: astore #7
/*     */     //   57: aload_0
/*     */     //   58: getfield temp6 : Lcom/badlogic/gdx/graphics/Color;
/*     */     //   61: astore #8
/*     */     //   63: aload_0
/*     */     //   64: getfield vertexEffect : Lcom/esotericsoftware/spine38/SkeletonMeshRenderer$VertexEffect;
/*     */     //   67: astore #9
/*     */     //   69: aload #9
/*     */     //   71: ifnull -> 82
/*     */     //   74: aload #9
/*     */     //   76: aload_2
/*     */     //   77: invokeinterface begin : (Lcom/esotericsoftware/spine38/Skeleton;)V
/*     */     //   82: aload_0
/*     */     //   83: getfield premultipliedAlpha : Z
/*     */     //   86: istore #10
/*     */     //   88: aconst_null
/*     */     //   89: astore #11
/*     */     //   91: iconst_0
/*     */     //   92: istore #12
/*     */     //   94: aconst_null
/*     */     //   95: astore #13
/*     */     //   97: aconst_null
/*     */     //   98: astore #14
/*     */     //   100: aconst_null
/*     */     //   101: astore #15
/*     */     //   103: aconst_null
/*     */     //   104: astore #16
/*     */     //   106: aload_2
/*     */     //   107: getfield color : Lcom/badlogic/gdx/graphics/Color;
/*     */     //   110: astore #17
/*     */     //   112: aload #17
/*     */     //   114: getfield r : F
/*     */     //   117: fstore #18
/*     */     //   119: aload #17
/*     */     //   121: getfield g : F
/*     */     //   124: fstore #19
/*     */     //   126: aload #17
/*     */     //   128: getfield b : F
/*     */     //   131: fstore #20
/*     */     //   133: aload #17
/*     */     //   135: getfield a : F
/*     */     //   138: fstore #21
/*     */     //   140: aload_2
/*     */     //   141: getfield drawOrder : Lcom/badlogic/gdx/utils/Array;
/*     */     //   144: astore #22
/*     */     //   146: iconst_0
/*     */     //   147: istore #23
/*     */     //   149: aload #22
/*     */     //   151: getfield size : I
/*     */     //   154: istore #24
/*     */     //   156: iload #23
/*     */     //   158: iload #24
/*     */     //   160: if_icmpge -> 988
/*     */     //   163: aload #22
/*     */     //   165: iload #23
/*     */     //   167: invokevirtual get : (I)Ljava/lang/Object;
/*     */     //   170: checkcast com/esotericsoftware/spine38/Slot
/*     */     //   173: astore #25
/*     */     //   175: aload #25
/*     */     //   177: getfield bone : Lcom/esotericsoftware/spine38/Bone;
/*     */     //   180: getfield active : Z
/*     */     //   183: ifne -> 189
/*     */     //   186: goto -> 982
/*     */     //   189: aconst_null
/*     */     //   190: astore #26
/*     */     //   192: aload_0
/*     */     //   193: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   196: invokevirtual isClipping : ()Z
/*     */     //   199: ifeq -> 206
/*     */     //   202: iconst_2
/*     */     //   203: goto -> 207
/*     */     //   206: iconst_5
/*     */     //   207: istore #27
/*     */     //   209: aload #25
/*     */     //   211: getfield attachment : Lcom/esotericsoftware/spine38/attachments/Attachment;
/*     */     //   214: astore #28
/*     */     //   216: aload #28
/*     */     //   218: instanceof com/esotericsoftware/spine38/attachments/RegionAttachment
/*     */     //   221: ifeq -> 293
/*     */     //   224: aload #28
/*     */     //   226: checkcast com/esotericsoftware/spine38/attachments/RegionAttachment
/*     */     //   229: astore #29
/*     */     //   231: iload #27
/*     */     //   233: iconst_2
/*     */     //   234: ishl
/*     */     //   235: istore #12
/*     */     //   237: aload_0
/*     */     //   238: getfield vertices : Lcom/badlogic/gdx/utils/FloatArray;
/*     */     //   241: getfield items : [F
/*     */     //   244: astore #13
/*     */     //   246: aload #29
/*     */     //   248: aload #25
/*     */     //   250: invokevirtual getBone : ()Lcom/esotericsoftware/spine38/Bone;
/*     */     //   253: aload #13
/*     */     //   255: iconst_0
/*     */     //   256: iload #27
/*     */     //   258: invokevirtual computeWorldVertices : (Lcom/esotericsoftware/spine38/Bone;[FII)V
/*     */     //   261: getstatic com/esotericsoftware/spine38/SkeletonMeshRenderer.quadTriangles : [S
/*     */     //   264: astore #15
/*     */     //   266: aload #29
/*     */     //   268: invokevirtual getRegion : ()Lcom/badlogic/gdx/graphics/g2d/TextureRegion;
/*     */     //   271: invokevirtual getTexture : ()Lcom/badlogic/gdx/graphics/Texture;
/*     */     //   274: astore #26
/*     */     //   276: aload #29
/*     */     //   278: invokevirtual getUVs : ()[F
/*     */     //   281: astore #14
/*     */     //   283: aload #29
/*     */     //   285: invokevirtual getColor : ()Lcom/badlogic/gdx/graphics/Color;
/*     */     //   288: astore #16
/*     */     //   290: goto -> 444
/*     */     //   293: aload #28
/*     */     //   295: instanceof com/esotericsoftware/spine38/attachments/MeshAttachment
/*     */     //   298: ifeq -> 384
/*     */     //   301: aload #28
/*     */     //   303: checkcast com/esotericsoftware/spine38/attachments/MeshAttachment
/*     */     //   306: astore #29
/*     */     //   308: aload #29
/*     */     //   310: invokevirtual getWorldVerticesLength : ()I
/*     */     //   313: istore #30
/*     */     //   315: iload #30
/*     */     //   317: iconst_1
/*     */     //   318: ishr
/*     */     //   319: iload #27
/*     */     //   321: imul
/*     */     //   322: istore #12
/*     */     //   324: aload_0
/*     */     //   325: getfield vertices : Lcom/badlogic/gdx/utils/FloatArray;
/*     */     //   328: iload #12
/*     */     //   330: invokevirtual setSize : (I)[F
/*     */     //   333: astore #13
/*     */     //   335: aload #29
/*     */     //   337: aload #25
/*     */     //   339: iconst_0
/*     */     //   340: iload #30
/*     */     //   342: aload #13
/*     */     //   344: iconst_0
/*     */     //   345: iload #27
/*     */     //   347: invokevirtual computeWorldVertices : (Lcom/esotericsoftware/spine38/Slot;II[FII)V
/*     */     //   350: aload #29
/*     */     //   352: invokevirtual getTriangles : ()[S
/*     */     //   355: astore #15
/*     */     //   357: aload #29
/*     */     //   359: invokevirtual getRegion : ()Lcom/badlogic/gdx/graphics/g2d/TextureRegion;
/*     */     //   362: invokevirtual getTexture : ()Lcom/badlogic/gdx/graphics/Texture;
/*     */     //   365: astore #26
/*     */     //   367: aload #29
/*     */     //   369: invokevirtual getUVs : ()[F
/*     */     //   372: astore #14
/*     */     //   374: aload #29
/*     */     //   376: invokevirtual getColor : ()Lcom/badlogic/gdx/graphics/Color;
/*     */     //   379: astore #16
/*     */     //   381: goto -> 444
/*     */     //   384: aload #28
/*     */     //   386: instanceof com/esotericsoftware/spine38/attachments/ClippingAttachment
/*     */     //   389: ifeq -> 414
/*     */     //   392: aload #28
/*     */     //   394: checkcast com/esotericsoftware/spine38/attachments/ClippingAttachment
/*     */     //   397: astore #29
/*     */     //   399: aload_0
/*     */     //   400: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   403: aload #25
/*     */     //   405: aload #29
/*     */     //   407: invokevirtual clipStart : (Lcom/esotericsoftware/spine38/Slot;Lcom/esotericsoftware/spine38/attachments/ClippingAttachment;)I
/*     */     //   410: pop
/*     */     //   411: goto -> 982
/*     */     //   414: aload #28
/*     */     //   416: instanceof com/esotericsoftware/spine38/attachments/SkeletonAttachment
/*     */     //   419: ifeq -> 444
/*     */     //   422: aload #28
/*     */     //   424: checkcast com/esotericsoftware/spine38/attachments/SkeletonAttachment
/*     */     //   427: invokevirtual getSkeleton : ()Lcom/esotericsoftware/spine38/Skeleton;
/*     */     //   430: astore #29
/*     */     //   432: aload #29
/*     */     //   434: ifnull -> 444
/*     */     //   437: aload_0
/*     */     //   438: aload_1
/*     */     //   439: aload #29
/*     */     //   441: invokevirtual draw : (Lcom/badlogic/gdx/graphics/g2d/PolygonSpriteBatch;Lcom/esotericsoftware/spine38/Skeleton;)V
/*     */     //   444: aload #26
/*     */     //   446: ifnull -> 973
/*     */     //   449: aload #25
/*     */     //   451: invokevirtual getColor : ()Lcom/badlogic/gdx/graphics/Color;
/*     */     //   454: astore #29
/*     */     //   456: fload #21
/*     */     //   458: aload #29
/*     */     //   460: getfield a : F
/*     */     //   463: fmul
/*     */     //   464: aload #16
/*     */     //   466: getfield a : F
/*     */     //   469: fmul
/*     */     //   470: ldc 255.0
/*     */     //   472: fmul
/*     */     //   473: fstore #30
/*     */     //   475: iload #10
/*     */     //   477: ifeq -> 485
/*     */     //   480: fload #30
/*     */     //   482: goto -> 487
/*     */     //   485: ldc 255.0
/*     */     //   487: fstore #31
/*     */     //   489: aload #25
/*     */     //   491: getfield data : Lcom/esotericsoftware/spine38/SlotData;
/*     */     //   494: invokevirtual getBlendMode : ()Lcom/esotericsoftware/spine38/BlendMode;
/*     */     //   497: astore #32
/*     */     //   499: aload #32
/*     */     //   501: aload #11
/*     */     //   503: if_acmpeq -> 547
/*     */     //   506: aload #32
/*     */     //   508: getstatic com/esotericsoftware/spine38/BlendMode.additive : Lcom/esotericsoftware/spine38/BlendMode;
/*     */     //   511: if_acmpne -> 527
/*     */     //   514: iload #10
/*     */     //   516: ifeq -> 527
/*     */     //   519: getstatic com/esotericsoftware/spine38/BlendMode.normal : Lcom/esotericsoftware/spine38/BlendMode;
/*     */     //   522: astore #32
/*     */     //   524: fconst_0
/*     */     //   525: fstore #30
/*     */     //   527: aload #32
/*     */     //   529: astore #11
/*     */     //   531: aload_1
/*     */     //   532: aload #11
/*     */     //   534: iload #10
/*     */     //   536: invokevirtual getSource : (Z)I
/*     */     //   539: aload #11
/*     */     //   541: invokevirtual getDest : ()I
/*     */     //   544: invokevirtual setBlendFunction : (II)V
/*     */     //   547: fload #30
/*     */     //   549: f2i
/*     */     //   550: bipush #24
/*     */     //   552: ishl
/*     */     //   553: fload #20
/*     */     //   555: aload #29
/*     */     //   557: getfield b : F
/*     */     //   560: fmul
/*     */     //   561: aload #16
/*     */     //   563: getfield b : F
/*     */     //   566: fmul
/*     */     //   567: fload #31
/*     */     //   569: fmul
/*     */     //   570: f2i
/*     */     //   571: bipush #16
/*     */     //   573: ishl
/*     */     //   574: ior
/*     */     //   575: fload #19
/*     */     //   577: aload #29
/*     */     //   579: getfield g : F
/*     */     //   582: fmul
/*     */     //   583: aload #16
/*     */     //   585: getfield g : F
/*     */     //   588: fmul
/*     */     //   589: fload #31
/*     */     //   591: fmul
/*     */     //   592: f2i
/*     */     //   593: bipush #8
/*     */     //   595: ishl
/*     */     //   596: ior
/*     */     //   597: fload #18
/*     */     //   599: aload #29
/*     */     //   601: getfield r : F
/*     */     //   604: fmul
/*     */     //   605: aload #16
/*     */     //   607: getfield r : F
/*     */     //   610: fmul
/*     */     //   611: fload #31
/*     */     //   613: fmul
/*     */     //   614: f2i
/*     */     //   615: ior
/*     */     //   616: invokestatic intToFloatColor : (I)F
/*     */     //   619: fstore #33
/*     */     //   621: aload_0
/*     */     //   622: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   625: invokevirtual isClipping : ()Z
/*     */     //   628: ifeq -> 725
/*     */     //   631: aload_0
/*     */     //   632: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   635: aload #13
/*     */     //   637: iload #12
/*     */     //   639: aload #15
/*     */     //   641: aload #15
/*     */     //   643: arraylength
/*     */     //   644: aload #14
/*     */     //   646: fload #33
/*     */     //   648: fconst_0
/*     */     //   649: iconst_0
/*     */     //   650: invokevirtual clipTriangles : ([FI[SI[FFFZ)V
/*     */     //   653: aload_0
/*     */     //   654: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   657: invokevirtual getClippedVertices : ()Lcom/badlogic/gdx/utils/FloatArray;
/*     */     //   660: astore #34
/*     */     //   662: aload_0
/*     */     //   663: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   666: invokevirtual getClippedTriangles : ()Lcom/badlogic/gdx/utils/ShortArray;
/*     */     //   669: astore #35
/*     */     //   671: aload #9
/*     */     //   673: ifnull -> 694
/*     */     //   676: aload_0
/*     */     //   677: aload #34
/*     */     //   679: getfield items : [F
/*     */     //   682: aload #34
/*     */     //   684: getfield size : I
/*     */     //   687: iconst_5
/*     */     //   688: fload #33
/*     */     //   690: fconst_0
/*     */     //   691: invokespecial applyVertexEffect : ([FIIFF)V
/*     */     //   694: aload_1
/*     */     //   695: aload #26
/*     */     //   697: aload #34
/*     */     //   699: getfield items : [F
/*     */     //   702: iconst_0
/*     */     //   703: aload #34
/*     */     //   705: getfield size : I
/*     */     //   708: aload #35
/*     */     //   710: getfield items : [S
/*     */     //   713: iconst_0
/*     */     //   714: aload #35
/*     */     //   716: getfield size : I
/*     */     //   719: invokevirtual draw : (Lcom/badlogic/gdx/graphics/Texture;[FII[SII)V
/*     */     //   722: goto -> 973
/*     */     //   725: aload #9
/*     */     //   727: ifnull -> 901
/*     */     //   730: aload #5
/*     */     //   732: fload #33
/*     */     //   734: invokestatic floatToIntColor : (F)I
/*     */     //   737: invokevirtual set : (I)Lcom/badlogic/gdx/graphics/Color;
/*     */     //   740: pop
/*     */     //   741: aload #6
/*     */     //   743: iconst_0
/*     */     //   744: invokevirtual set : (I)Lcom/badlogic/gdx/graphics/Color;
/*     */     //   747: pop
/*     */     //   748: iconst_0
/*     */     //   749: istore #34
/*     */     //   751: iconst_0
/*     */     //   752: istore #35
/*     */     //   754: iload #34
/*     */     //   756: iload #12
/*     */     //   758: if_icmpge -> 898
/*     */     //   761: aload_3
/*     */     //   762: aload #13
/*     */     //   764: iload #34
/*     */     //   766: faload
/*     */     //   767: putfield x : F
/*     */     //   770: aload_3
/*     */     //   771: aload #13
/*     */     //   773: iload #34
/*     */     //   775: iconst_1
/*     */     //   776: iadd
/*     */     //   777: faload
/*     */     //   778: putfield y : F
/*     */     //   781: aload #7
/*     */     //   783: aload #5
/*     */     //   785: invokevirtual set : (Lcom/badlogic/gdx/graphics/Color;)Lcom/badlogic/gdx/graphics/Color;
/*     */     //   788: pop
/*     */     //   789: aload #8
/*     */     //   791: aload #6
/*     */     //   793: invokevirtual set : (Lcom/badlogic/gdx/graphics/Color;)Lcom/badlogic/gdx/graphics/Color;
/*     */     //   796: pop
/*     */     //   797: aload #4
/*     */     //   799: aload #14
/*     */     //   801: iload #35
/*     */     //   803: faload
/*     */     //   804: putfield x : F
/*     */     //   807: aload #4
/*     */     //   809: aload #14
/*     */     //   811: iload #35
/*     */     //   813: iconst_1
/*     */     //   814: iadd
/*     */     //   815: faload
/*     */     //   816: putfield y : F
/*     */     //   819: aload #9
/*     */     //   821: aload_3
/*     */     //   822: aload #4
/*     */     //   824: aload #7
/*     */     //   826: aload #8
/*     */     //   828: invokeinterface transform : (Lcom/badlogic/gdx/math/Vector2;Lcom/badlogic/gdx/math/Vector2;Lcom/badlogic/gdx/graphics/Color;Lcom/badlogic/gdx/graphics/Color;)V
/*     */     //   833: aload #13
/*     */     //   835: iload #34
/*     */     //   837: aload_3
/*     */     //   838: getfield x : F
/*     */     //   841: fastore
/*     */     //   842: aload #13
/*     */     //   844: iload #34
/*     */     //   846: iconst_1
/*     */     //   847: iadd
/*     */     //   848: aload_3
/*     */     //   849: getfield y : F
/*     */     //   852: fastore
/*     */     //   853: aload #13
/*     */     //   855: iload #34
/*     */     //   857: iconst_2
/*     */     //   858: iadd
/*     */     //   859: aload #7
/*     */     //   861: invokevirtual toFloatBits : ()F
/*     */     //   864: fastore
/*     */     //   865: aload #13
/*     */     //   867: iload #34
/*     */     //   869: iconst_3
/*     */     //   870: iadd
/*     */     //   871: aload #4
/*     */     //   873: getfield x : F
/*     */     //   876: fastore
/*     */     //   877: aload #13
/*     */     //   879: iload #34
/*     */     //   881: iconst_4
/*     */     //   882: iadd
/*     */     //   883: aload #4
/*     */     //   885: getfield y : F
/*     */     //   888: fastore
/*     */     //   889: iinc #34, 5
/*     */     //   892: iinc #35, 2
/*     */     //   895: goto -> 754
/*     */     //   898: goto -> 956
/*     */     //   901: iconst_2
/*     */     //   902: istore #34
/*     */     //   904: iconst_0
/*     */     //   905: istore #35
/*     */     //   907: iload #34
/*     */     //   909: iload #12
/*     */     //   911: if_icmpge -> 956
/*     */     //   914: aload #13
/*     */     //   916: iload #34
/*     */     //   918: fload #33
/*     */     //   920: fastore
/*     */     //   921: aload #13
/*     */     //   923: iload #34
/*     */     //   925: iconst_1
/*     */     //   926: iadd
/*     */     //   927: aload #14
/*     */     //   929: iload #35
/*     */     //   931: faload
/*     */     //   932: fastore
/*     */     //   933: aload #13
/*     */     //   935: iload #34
/*     */     //   937: iconst_2
/*     */     //   938: iadd
/*     */     //   939: aload #14
/*     */     //   941: iload #35
/*     */     //   943: iconst_1
/*     */     //   944: iadd
/*     */     //   945: faload
/*     */     //   946: fastore
/*     */     //   947: iinc #34, 5
/*     */     //   950: iinc #35, 2
/*     */     //   953: goto -> 907
/*     */     //   956: aload_1
/*     */     //   957: aload #26
/*     */     //   959: aload #13
/*     */     //   961: iconst_0
/*     */     //   962: iload #12
/*     */     //   964: aload #15
/*     */     //   966: iconst_0
/*     */     //   967: aload #15
/*     */     //   969: arraylength
/*     */     //   970: invokevirtual draw : (Lcom/badlogic/gdx/graphics/Texture;[FII[SII)V
/*     */     //   973: aload_0
/*     */     //   974: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   977: aload #25
/*     */     //   979: invokevirtual clipEnd : (Lcom/esotericsoftware/spine38/Slot;)V
/*     */     //   982: iinc #23, 1
/*     */     //   985: goto -> 156
/*     */     //   988: aload_0
/*     */     //   989: getfield clipper : Lcom/esotericsoftware/spine38/utils/SkeletonClipping;
/*     */     //   992: invokevirtual clipEnd : ()V
/*     */     //   995: aload #9
/*     */     //   997: ifnull -> 1007
/*     */     //   1000: aload #9
/*     */     //   1002: invokeinterface end : ()V
/*     */     //   1007: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #146	-> 0
/*     */     //   #147	-> 14
/*     */     //   #149	-> 28
/*     */     //   #150	-> 39
/*     */     //   #151	-> 51
/*     */     //   #152	-> 63
/*     */     //   #153	-> 69
/*     */     //   #155	-> 82
/*     */     //   #156	-> 88
/*     */     //   #157	-> 91
/*     */     //   #158	-> 94
/*     */     //   #159	-> 100
/*     */     //   #160	-> 103
/*     */     //   #161	-> 112
/*     */     //   #162	-> 140
/*     */     //   #163	-> 146
/*     */     //   #164	-> 163
/*     */     //   #165	-> 175
/*     */     //   #166	-> 189
/*     */     //   #167	-> 192
/*     */     //   #168	-> 209
/*     */     //   #169	-> 216
/*     */     //   #170	-> 224
/*     */     //   #171	-> 231
/*     */     //   #172	-> 237
/*     */     //   #173	-> 246
/*     */     //   #174	-> 261
/*     */     //   #175	-> 266
/*     */     //   #176	-> 276
/*     */     //   #177	-> 283
/*     */     //   #179	-> 290
/*     */     //   #180	-> 301
/*     */     //   #181	-> 308
/*     */     //   #182	-> 315
/*     */     //   #183	-> 324
/*     */     //   #184	-> 335
/*     */     //   #185	-> 350
/*     */     //   #186	-> 357
/*     */     //   #187	-> 367
/*     */     //   #188	-> 374
/*     */     //   #190	-> 381
/*     */     //   #191	-> 392
/*     */     //   #192	-> 399
/*     */     //   #193	-> 411
/*     */     //   #195	-> 414
/*     */     //   #196	-> 422
/*     */     //   #197	-> 432
/*     */     //   #200	-> 444
/*     */     //   #201	-> 449
/*     */     //   #202	-> 456
/*     */     //   #203	-> 475
/*     */     //   #205	-> 489
/*     */     //   #206	-> 499
/*     */     //   #207	-> 506
/*     */     //   #208	-> 519
/*     */     //   #209	-> 524
/*     */     //   #211	-> 527
/*     */     //   #212	-> 531
/*     */     //   #215	-> 547
/*     */     //   #220	-> 621
/*     */     //   #221	-> 631
/*     */     //   #222	-> 653
/*     */     //   #223	-> 662
/*     */     //   #224	-> 671
/*     */     //   #225	-> 694
/*     */     //   #227	-> 722
/*     */     //   #228	-> 725
/*     */     //   #229	-> 730
/*     */     //   #230	-> 741
/*     */     //   #231	-> 748
/*     */     //   #232	-> 761
/*     */     //   #233	-> 770
/*     */     //   #234	-> 781
/*     */     //   #235	-> 789
/*     */     //   #236	-> 797
/*     */     //   #237	-> 807
/*     */     //   #238	-> 819
/*     */     //   #239	-> 833
/*     */     //   #240	-> 842
/*     */     //   #241	-> 853
/*     */     //   #242	-> 865
/*     */     //   #243	-> 877
/*     */     //   #231	-> 889
/*     */     //   #246	-> 901
/*     */     //   #247	-> 914
/*     */     //   #248	-> 921
/*     */     //   #249	-> 933
/*     */     //   #246	-> 947
/*     */     //   #252	-> 956
/*     */     //   #256	-> 973
/*     */     //   #163	-> 982
/*     */     //   #258	-> 988
/*     */     //   #259	-> 995
/*     */     //   #260	-> 1007
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   231	59	29	region	Lcom/esotericsoftware/spine38/attachments/RegionAttachment;
/*     */     //   308	73	29	mesh	Lcom/esotericsoftware/spine38/attachments/MeshAttachment;
/*     */     //   315	66	30	count	I
/*     */     //   399	15	29	clip	Lcom/esotericsoftware/spine38/attachments/ClippingAttachment;
/*     */     //   432	12	29	attachmentSkeleton	Lcom/esotericsoftware/spine38/Skeleton;
/*     */     //   662	60	34	clippedVertices	Lcom/badlogic/gdx/utils/FloatArray;
/*     */     //   671	51	35	clippedTriangles	Lcom/badlogic/gdx/utils/ShortArray;
/*     */     //   751	147	34	v	I
/*     */     //   754	144	35	u	I
/*     */     //   904	52	34	v	I
/*     */     //   907	49	35	u	I
/*     */     //   456	517	29	slotColor	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   475	498	30	alpha	F
/*     */     //   489	484	31	multiplier	F
/*     */     //   499	474	32	slotBlendMode	Lcom/esotericsoftware/spine38/BlendMode;
/*     */     //   621	352	33	c	F
/*     */     //   175	807	25	slot	Lcom/esotericsoftware/spine38/Slot;
/*     */     //   192	790	26	texture	Lcom/badlogic/gdx/graphics/Texture;
/*     */     //   209	773	27	vertexSize	I
/*     */     //   216	766	28	attachment	Lcom/esotericsoftware/spine38/attachments/Attachment;
/*     */     //   149	839	23	i	I
/*     */     //   156	832	24	n	I
/*     */     //   0	1008	0	this	Lcom/esotericsoftware/spine38/SkeletonMeshRenderer;
/*     */     //   0	1008	1	batch	Lcom/badlogic/gdx/graphics/g2d/PolygonSpriteBatch;
/*     */     //   0	1008	2	skeleton	Lcom/esotericsoftware/spine38/Skeleton;
/*     */     //   33	975	3	tempPosition	Lcom/badlogic/gdx/math/Vector2;
/*     */     //   39	969	4	tempUV	Lcom/badlogic/gdx/math/Vector2;
/*     */     //   45	963	5	tempLight1	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   51	957	6	tempDark1	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   57	951	7	tempLight2	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   63	945	8	tempDark2	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   69	939	9	vertexEffect	Lcom/esotericsoftware/spine38/SkeletonMeshRenderer$VertexEffect;
/*     */     //   88	920	10	premultipliedAlpha	Z
/*     */     //   91	917	11	blendMode	Lcom/esotericsoftware/spine38/BlendMode;
/*     */     //   94	914	12	verticesLength	I
/*     */     //   97	911	13	vertices	[F
/*     */     //   100	908	14	uvs	[F
/*     */     //   103	905	15	triangles	[S
/*     */     //   106	902	16	color	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   112	896	17	skeletonColor	Lcom/badlogic/gdx/graphics/Color;
/*     */     //   119	889	18	r	F
/*     */     //   126	882	19	g	F
/*     */     //   133	875	20	b	F
/*     */     //   140	868	21	a	F
/*     */     //   146	862	22	drawOrder	Lcom/badlogic/gdx/utils/Array;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   146	862	22	drawOrder	Lcom/badlogic/gdx/utils/Array<Lcom/esotericsoftware/spine38/Slot;>;
/*     */   }
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
/*     */   private void applyVertexEffect(float[] vertices, int verticesLength, int stride, float light, float dark) {
/* 263 */     Vector2 tempPosition = this.temp, tempUV = this.temp2;
/* 264 */     Color tempLight1 = this.temp3, tempDark1 = this.temp4;
/* 265 */     Color tempLight2 = this.temp5, tempDark2 = this.temp6;
/* 266 */     VertexEffect vertexEffect = this.vertexEffect;
/* 267 */     tempLight1.set(NumberUtils.floatToIntColor(light));
/* 268 */     tempDark1.set(NumberUtils.floatToIntColor(dark));
/* 269 */     if (stride == 5) {
/* 270 */       int v; for (v = 0; v < verticesLength; v += stride) {
/* 271 */         tempPosition.x = vertices[v];
/* 272 */         tempPosition.y = vertices[v + 1];
/* 273 */         tempUV.x = vertices[v + 3];
/* 274 */         tempUV.y = vertices[v + 4];
/* 275 */         tempLight2.set(tempLight1);
/* 276 */         tempDark2.set(tempDark1);
/* 277 */         vertexEffect.transform(tempPosition, tempUV, tempLight2, tempDark2);
/* 278 */         vertices[v] = tempPosition.x;
/* 279 */         vertices[v + 1] = tempPosition.y;
/* 280 */         vertices[v + 2] = tempLight2.toFloatBits();
/* 281 */         vertices[v + 3] = tempUV.x;
/* 282 */         vertices[v + 4] = tempUV.y;
/*     */       } 
/*     */     } else {
/* 285 */       int v; for (v = 0; v < verticesLength; v += stride) {
/* 286 */         tempPosition.x = vertices[v];
/* 287 */         tempPosition.y = vertices[v + 1];
/* 288 */         tempUV.x = vertices[v + 4];
/* 289 */         tempUV.y = vertices[v + 5];
/* 290 */         tempLight2.set(tempLight1);
/* 291 */         tempDark2.set(tempDark1);
/* 292 */         vertexEffect.transform(tempPosition, tempUV, tempLight2, tempDark2);
/* 293 */         vertices[v] = tempPosition.x;
/* 294 */         vertices[v + 1] = tempPosition.y;
/* 295 */         vertices[v + 2] = tempLight2.toFloatBits();
/* 296 */         vertices[v + 3] = tempDark2.toFloatBits();
/* 297 */         vertices[v + 4] = tempUV.x;
/* 298 */         vertices[v + 5] = tempUV.y;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean getPremultipliedAlpha() {
/* 304 */     return this.premultipliedAlpha;
/*     */   }
/*     */   
/*     */   public void setPremultipliedAlpha(boolean premultipliedAlpha) {
/* 308 */     this.premultipliedAlpha = premultipliedAlpha;
/*     */   }
/*     */ 
/*     */   
/*     */   public VertexEffect getVertexEffect() {
/* 313 */     return this.vertexEffect;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setVertexEffect(VertexEffect vertexEffect) {
/* 318 */     this.vertexEffect = vertexEffect;
/*     */   }
/*     */   
/*     */   public static interface VertexEffect {
/*     */     void begin(Skeleton param1Skeleton);
/*     */     
/*     */     void transform(Vector2 param1Vector21, Vector2 param1Vector22, Color param1Color1, Color param1Color2);
/*     */     
/*     */     void end();
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SkeletonMeshRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */