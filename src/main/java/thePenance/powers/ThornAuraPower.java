package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;
import thePenance.monsters.VolsiniiCivilian;

import java.util.ArrayList;

public class ThornAuraPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("ThornAuraPower");
    // 移除 PowerStrings, NAME, DESCRIPTIONS，BasePower 已自动处理

    public ThornAuraPower(AbstractCreature owner, int amount) {
        super(
                POWER_ID,          // ID
                PowerType.BUFF,    // 类型
                false,             // isTurnBased
                owner,             // 拥有者
                null,              // source
                amount,            // 层数
                true,              // initDescription
                true              // loadImage -> 设为 false，禁止自动找图
        );
    }

    @Override
    public void updateDescription() {
        // 使用父类 BasePower 提供的 DESCRIPTIONS
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.flash();

            // 1. 先生成标准的伤害矩阵
            // 这会根据层数计算出对每个位置怪物的伤害
            int[] damageMatrix = DamageInfo.createDamageMatrix(this.amount, true);

            // 2. 遍历怪物列表，找到平民，将对应位置的伤害设为 0
            ArrayList<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
            for (int i = 0; i < monsters.size(); i++) {
                AbstractMonster m = monsters.get(i);

                // 判断是否是平民 (即使平民是无敌的，这里显式设为0可以避免触发受伤动画或破防音效)
                if (m instanceof VolsiniiCivilian) {
                    damageMatrix[i] = 0;
                }
            }

            // 3. 使用修改后的 damageMatrix 发起攻击
            // 注意：建议第一个参数传 player 而不是 null，这样这部分伤害会被视为“来自玩家的来源”
            this.addToBot(new DamageAllEnemiesAction(
                    AbstractDungeon.player,
                    damageMatrix,
                    DamageInfo.DamageType.THORNS,
                    AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new ThornAuraPower(owner, amount);
    }
}