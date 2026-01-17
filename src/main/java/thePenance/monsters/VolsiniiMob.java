package thePenance.monsters;

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
import thePenance.powers.PlayerFacingPower; // ★★★ 确保你有这个 Power 类，否则删掉相关代码 ★★★

public class VolsiniiMob extends AbstractMonster {
    public static final String ID = PenanceMod.makeID("VolsiniiMob");
    // 这里的 ID "thePenance:VolsiniiMob" 必须对应 MonstersStrings.json 里的键名
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
        super(monsterStrings.NAME, ID, 60, 0.0F, 0.0F, 150.0F, 220.0F, null, x, y);
        this.mobType = type;

        if (this.mobType == TYPE_AGILE) {
            this.setHp(42);
            this.damage.add(new DamageInfo(this, 4));
            this.damage.add(new DamageInfo(this, 10));
            this.loadAnimation("images/monsters/theBottom/angryGremlin/skeleton.atlas", "images/monsters/theBottom/angryGremlin/skeleton.json", 1.0F);
            this.state.setAnimation(0, "idle", true);
            // 对应 JSON 里的 DIALOG[0]
            this.name = monsterStrings.DIALOG[0];
        } else {
            this.setHp(65);
            this.damage.add(new DamageInfo(this, 12));
            this.damage.add(new DamageInfo(this, 30));
            this.loadAnimation("images/monsters/theBottom/fatGremlin/skeleton.atlas", "images/monsters/theBottom/fatGremlin/skeleton.json", 1.0F);
            this.state.setAnimation(0, "idle", true);
            // 对应 JSON 里的 DIALOG[1]
            this.name = monsterStrings.DIALOG[1];
        }
    }

    @Override
    public void usePreBattleAction() {
        // 确保你的 PlayerFacingPower 路径正确
        if (!AbstractDungeon.player.hasPower(PlayerFacingPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new PlayerFacingPower(AbstractDungeon.player)));
        }
    }

    // ★★★ 修复后的核心方法 ★★★
    private void executeAttack(DamageInfo originalInfo, AbstractGameAction.AttackEffect effect) {
        // 获取玩家朝向
        PlayerFacingPower facingPower = (PlayerFacingPower) AbstractDungeon.player.getPower(PlayerFacingPower.POWER_ID);
        boolean backExposed = false;
        if (facingPower != null) {
            backExposed = facingPower.isBackExposedTo(this.drawX);
        }

        // 寻找平民
        AbstractMonster civilian = null;
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (m instanceof VolsiniiCivilian && !m.isDeadOrEscaped()) {
                civilian = m;
                break;
            }
        }

        if (backExposed) {
            if (civilian != null) {
                // === 1. 背对且平民活着：攻击平民 ===
                // 修正：直接创建新的 DamageInfo 传给 Action，不要调用不存在的 applyPowers
                DamageInfo civInfo = new DamageInfo(this, originalInfo.base, DamageInfo.DamageType.NORMAL);

                if (mobType == TYPE_AGILE) addToBot(new AnimateFastAttackAction(this));
                else addToBot(new AnimateSlowAttackAction(this));

                // 伤害动作会自动计算力量加成
                addToBot(new DamageAction(civilian, civInfo, effect));

            } else {
                // === 2. 背对且平民已死：背刺玩家 (双倍伤害) ===
                // 修正：简单粗暴地将基础伤害 x2，然后传入 DamageAction，让系统叠加力量
                // (Base * 2) + Strength
                DamageInfo backstabInfo = new DamageInfo(this, originalInfo.base * 2, DamageInfo.DamageType.NORMAL);

                if (mobType == TYPE_AGILE) addToBot(new AnimateFastAttackAction(this));
                else addToBot(new AnimateSlowAttackAction(this));

                addToBot(new DamageAction(AbstractDungeon.player, backstabInfo, AbstractGameAction.AttackEffect.SLASH_HEAVY));
            }
        } else {
            // === 3. 正面面对：正常攻击玩家 ===
            // 修正：创建新的 DamageInfo 实例以避免引用问题
            DamageInfo normalInfo = new DamageInfo(this, originalInfo.base, DamageInfo.DamageType.NORMAL);

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