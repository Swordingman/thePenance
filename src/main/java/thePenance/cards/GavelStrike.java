package thePenance.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect; // 这是一个类似巨锤砸下的特效
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class GavelStrike extends BaseCard {

    public static final String ID = makeID("GavelStrike");

    private static final int COST = 2;
    private static final int DAMAGE = 10;
    private static final int UPG_DAMAGE = 5; // 升级后 15 伤害

    public GavelStrike() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        setDamage(DAMAGE, UPG_DAMAGE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 获取当前的屏障值
        int currentBarrier = 0;
        if (p.hasPower(BarrierPower.POWER_ID)) {
            currentBarrier = p.getPower(BarrierPower.POWER_ID).amount;
        }

        // 2. 判断目标类型（不能是精英或Boss）
        boolean isBossOrElite = (m.type == AbstractMonster.EnemyType.BOSS || m.type == AbstractMonster.EnemyType.ELITE);

        // 3. 判定是否满足斩杀条件：非精英BOSS 且 屏障 > 怪血量
        if (!isBossOrElite && currentBarrier > m.currentHealth) {
            // --- 处决逻辑 ---
            if (m.currentHealth > 0) {
                // 视觉特效：重锤砸下 (Iron Wave的那个重击，或者是巨石的重击)
                addToBot(new VFXAction(new WeightyImpactEffect(m.hb.cX, m.hb.cY, Color.GOLD.cpy())));
                addToBot(new WaitAction(0.8F)); // 等一下特效

                // 执行处决动作
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        // 再次检查以防万一
                        if (!m.isDeadOrEscaped()) {
                            // 强制扣减生命至0并触发死亡
                            m.currentHealth = 0;
                            m.healthBarUpdatedEvent();
                            m.die(); // 触发怪物的死亡逻辑（掉落金币、遗物等）

                            // 无论如何，视为造成了巨大的伤害（用于统计数据）
                            // 如果你想让这次处决也能触发“造成伤害”的遗物，可以在这里补一个 huge DamageAction
                        }
                        this.isDone = true;
                    }
                });
            }
        } else {
            // --- 正常伤害逻辑 ---
            // 如果不满足斩杀，则造成普通伤害
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
        }
    }
}