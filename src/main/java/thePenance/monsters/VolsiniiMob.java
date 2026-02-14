package thePenance.monsters;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.PenanceMod;
import thePenance.powers.PlayerFacingPower;
import basemod.ReflectionHacks;

public class VolsiniiMob extends AbstractMonster {
    public static final String ID = PenanceMod.makeID("VolsiniiMob");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    public static final int TYPE_AGILE = 0;
    public static final int TYPE_HEAVY = 1;

    private int mobType;

    // 动作 ID
    private static final byte A_MULTI_ATTACK = 10;
    private static final byte A_NORMAL_ATTACK = 11;
    private static final byte A_BLOCK = 12;
    private static final byte A_BUFF_STR = 13;

    private static final byte B_NORMAL_ATTACK = 20;
    private static final byte B_HEAVY_ATTACK = 21;
    private static final byte B_BLOCK = 22;
    private static final byte B_BLOCK_STR = 23;
    private static final byte B_BUFF_STR = 24;

    public VolsiniiMob(float x, float y, int type) {
        // imgUrl 设为 null，碰撞箱尺寸保持原样 (如果你发现点击判定不对，调整 150.0F 和 220.0F)
        super(monsterStrings.NAME, ID, 60, 0.0F, 0.0F, 150.0F, 220.0F, null, x, y);
        this.mobType = type;

        if (this.mobType == TYPE_AGILE) {
            // === 敏捷型 (Sarkaz Guerilla Bowman?) ===
            this.setHp(42);
            // 伤害索引 0: Multi-Attack (4x3)
            this.damage.add(new DamageInfo(this, 4));
            // 伤害索引 1: Normal Attack (10)
            this.damage.add(new DamageInfo(this, 10));

            // 加载 Agile 动画
            this.loadAnimation(
                    "thePenance/images/enemies/VolsiniiMob/enemy_1278_sgbow.atlas",
                    "thePenance/images/enemies/VolsiniiMob/enemy_1278_sgbow.json",
                    1.5F // 比例 Scale：如果觉得太小，可以改成 1.5F
            );

            // 设置初始动画 (请确保 Json 中有 "Idle" 这个动作名，否则会崩溃)
            AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
            e.setTime(e.getEndTime() * MathUtils.random()); // 随机偏移，防止整齐划一

            this.name = monsterStrings.DIALOG[0];

            this.flipHorizontal = false;
        } else {
            // === 重装型 (Sarkaz Guerilla Shieldguard?) ===
            this.setHp(65);
            // 伤害索引 0: Normal Attack (12)
            this.damage.add(new DamageInfo(this, 12));
            // 伤害索引 1: Heavy Attack (30)
            this.damage.add(new DamageInfo(this, 30));

            // 加载 Heavy 动画
            this.loadAnimation(
                    "thePenance/images/enemies/VolsiniiMob/enemy_1279_sgbdg.atlas",
                    "thePenance/images/enemies/VolsiniiMob/enemy_1279_sgbdg.json",
                    1.2F // 比例 Scale：通常重装单位可以稍微大一点，试试 1.2F 或 1.3F
            );

            // 设置初始动画
            AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
            e.setTime(e.getEndTime() * MathUtils.random());

            this.name = monsterStrings.DIALOG[1];
        }

        // 如果素材默认是面朝右的（大多数明日方舟SD小人默认朝左，不需要这行），
        // 发现朝向反了就把下面这一行取消注释：
        this.flipHorizontal = true;
    }

    @Override
    public void usePreBattleAction() {
        if (!AbstractDungeon.player.hasPower(PlayerFacingPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new PlayerFacingPower(AbstractDungeon.player)));
        }
    }

    /**
     * 辅助方法：判断是否应该背刺玩家（双倍伤害）。
     * 条件：1. 没有平民活着 2. 玩家背对当前怪物
     */
    private boolean shouldBackstabPlayer() {
        // 1. 检查平民是否存活
        boolean civilianAlive = false;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m instanceof VolsiniiCivilian && !m.isDeadOrEscaped()) {
                civilianAlive = true;
                break;
            }
        }
        if (civilianAlive) return false;

        // 2. 检查玩家朝向
        if (AbstractDungeon.player.hasPower(PlayerFacingPower.POWER_ID)) {
            PlayerFacingPower facingPower = (PlayerFacingPower) AbstractDungeon.player.getPower(PlayerFacingPower.POWER_ID);
            return facingPower.isBackExposedTo(this.drawX);
        }
        return false;
    }

    @Override
    public void applyPowers() {
        super.applyPowers();

        if (this.intent != Intent.ATTACK && this.intent != Intent.ATTACK_BUFF && this.intent != Intent.ATTACK_DEBUFF && this.intent != Intent.ATTACK_DEFEND) {
            return;
        }

        // 修改：将倍率从 2 (双倍) 改为 1.5 (后方攻击机制)
        if (shouldBackstabPlayer()) {
            int currentBaseDamage = 0;
            if (this.mobType == TYPE_AGILE) {
                if (this.nextMove == A_MULTI_ATTACK) currentBaseDamage = this.damage.get(0).base;
                else if (this.nextMove == A_NORMAL_ATTACK) currentBaseDamage = this.damage.get(1).base;
            } else {
                if (this.nextMove == B_NORMAL_ATTACK) currentBaseDamage = this.damage.get(0).base;
                else if (this.nextMove == B_HEAVY_ATTACK) currentBaseDamage = this.damage.get(1).base;
            }

            // 此处应用 1.5 倍伤害
            int backAttackDmg = (int)(currentBaseDamage * 1.5F);
            DamageInfo info = new DamageInfo(this, backAttackDmg, DamageInfo.DamageType.NORMAL);
            info.applyPowers(this, AbstractDungeon.player);

            ReflectionHacks.setPrivate(this, AbstractMonster.class, "intentDmg", info.output);
        }
    }

    private void executeAttack(DamageInfo originalInfo, AbstractGameAction.AttackEffect effect) {
        // 再次获取相关状态，判断攻击逻辑
        AbstractMonster civilian = null;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m instanceof VolsiniiCivilian && !m.isDeadOrEscaped()) {
                civilian = m;
                break;
            }
        }

        PlayerFacingPower facingPower = (PlayerFacingPower) AbstractDungeon.player.getPower(PlayerFacingPower.POWER_ID);
        boolean backExposed = (facingPower != null && facingPower.isBackExposedTo(this.drawX));

        if (backExposed && civilian != null) {
            // === 情况 1: 玩家背对，但有平民存活 -> 攻击平民 ===
            DamageInfo civInfo = new DamageInfo(this, originalInfo.base, DamageInfo.DamageType.NORMAL);
            civInfo.applyPowers(this, civilian);

            if (mobType == TYPE_AGILE) addToBot(new AnimateFastAttackAction(this));
            else addToBot(new AnimateSlowAttackAction(this));

            addToBot(new DamageAction(civilian, civInfo, effect));

        } else if (shouldBackstabPlayer()) {
            // === 情况 2: 玩家背对且无平民 -> 后方攻击玩家 (Base * 1.5) ===
            // 修改：将倍率从 2 改为 1.5
            int backAttackDmg = (int)(originalInfo.base * 1.5F);
            DamageInfo backstabInfo = new DamageInfo(this, backAttackDmg, DamageInfo.DamageType.NORMAL);
            backstabInfo.applyPowers(this, AbstractDungeon.player);

            if (mobType == TYPE_AGILE) addToBot(new AnimateFastAttackAction(this));
            else addToBot(new AnimateSlowAttackAction(this));

            // 音效可以保持 Slash Heavy 或视情况改为 Blunt Heavy
            addToBot(new DamageAction(AbstractDungeon.player, backstabInfo, AbstractGameAction.AttackEffect.SLASH_HEAVY));

        } else {
            // === 情况 3: 正常正面攻击 ===
            DamageInfo normalInfo = new DamageInfo(this, originalInfo.base, DamageInfo.DamageType.NORMAL);
            normalInfo.applyPowers(this, AbstractDungeon.player);

            if (mobType == TYPE_AGILE) addToBot(new AnimateFastAttackAction(this));
            else addToBot(new AnimateSlowAttackAction(this));

            addToBot(new DamageAction(AbstractDungeon.player, normalInfo, effect));
        }
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            // === A 类型 (Agile) ===
            case A_MULTI_ATTACK:
                for (int i = 0; i < 3; i++) {
                    executeAttack(this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                }
                break;
            case A_NORMAL_ATTACK:
                executeAttack(this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            case A_BLOCK:
                addToBot(new GainBlockAction(this, 8));
                break;
            case A_BUFF_STR:
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 2), 2));
                break;

            // === B 类型 (Heavy) ===
            case B_NORMAL_ATTACK:
                executeAttack(this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            case B_HEAVY_ATTACK:
                executeAttack(this.damage.get(1), AbstractGameAction.AttackEffect.SMASH);
                break;
            case B_BLOCK:
                addToBot(new GainBlockAction(this, 12));
                break;
            case B_BLOCK_STR:
                addToBot(new GainBlockAction(this, 8));
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 1), 1));
                break;
            case B_BUFF_STR:
                addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 3), 3));
                break;
        }

        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        // 简单的队友检测，用于判断攻击频率（逻辑保持原样）
        boolean partnerAlive = false;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m != this && !m.isDeadOrEscaped() && m instanceof VolsiniiMob) {
                partnerAlive = true;
                break;
            }
        }

        boolean shouldAttack;
        if (!partnerAlive) {
            shouldAttack = true;
        } else {
            int turn = AbstractDungeon.actionManager.turn;
            if (this.mobType == TYPE_AGILE) shouldAttack = (turn % 2 != 0);
            else shouldAttack = (turn % 2 == 0);
        }

        if (this.mobType == TYPE_AGILE) {
            if (shouldAttack) {
                if (num < 50) this.setMove(A_MULTI_ATTACK, Intent.ATTACK, this.damage.get(0).base, 3, true);
                else this.setMove(A_NORMAL_ATTACK, Intent.ATTACK, this.damage.get(1).base);
            } else {
                if (num < 50) this.setMove(A_BLOCK, Intent.DEFEND);
                else this.setMove(A_BUFF_STR, Intent.BUFF);
            }
        } else {
            if (shouldAttack) {
                if (num < 65) this.setMove(B_NORMAL_ATTACK, Intent.ATTACK, this.damage.get(0).base);
                else this.setMove(B_HEAVY_ATTACK, Intent.ATTACK, this.damage.get(1).base);
            } else {
                if (num < 40) this.setMove(B_BLOCK, Intent.DEFEND);
                else if (num < 80) this.setMove(B_BLOCK_STR, Intent.DEFEND_BUFF);
                else this.setMove(B_BUFF_STR, Intent.BUFF);
            }
        }
    }
}