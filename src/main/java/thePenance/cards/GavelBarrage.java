package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class GavelBarrage extends BaseCard {
    public static final String ID = makeID("GavelBarrage");
    private static final int COST = -1; // X费
    private static final int DAMAGE = 3;
    private static final int UPG_DAMAGE = 1; // 3->4

    public GavelBarrage() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.UNCOMMON,
                CardTarget.ENEMY,
                COST
        ));
        setDamage(DAMAGE, UPG_DAMAGE);
        setMagic(0, 1); // 基础0，升级+1
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (energyOnUse < -1) {
            energyOnUse = -1;
        }

        // 调用 X 费处理 Action
        addToBot(new GavelBarrageAction(p, m, damage, damageTypeForTurn, freeToPlayOnce, energyOnUse, upgraded));
    }

    // --- 内部 Action ---
    public static class GavelBarrageAction extends AbstractGameAction {
        private final boolean freeToPlayOnce;
        private final int energyOnUse;
        private final AbstractPlayer p;
        private final AbstractMonster m;
        private final int damage;
        private final DamageInfo.DamageType damageType;
        private final boolean upgraded;

        public GavelBarrageAction(AbstractPlayer p, AbstractMonster m, int damage, DamageInfo.DamageType damageType, boolean freeToPlayOnce, int energyOnUse, boolean upgraded) {
            this.p = p;
            this.m = m;
            this.damage = damage;
            this.damageType = damageType;
            this.freeToPlayOnce = freeToPlayOnce;
            this.energyOnUse = energyOnUse;
            this.upgraded = upgraded;
            this.actionType = ActionType.DAMAGE;
        }

        @Override
        public void update() {
            int effect = EnergyPanel.totalCount;
            if (this.energyOnUse != -1) {
                effect = this.energyOnUse;
            }

            // 处理 怀表/化学X 等遗物
            if (this.p.hasRelic(ChemicalX.ID)) {
                effect += 2;
                this.p.getRelic(ChemicalX.ID).flash();
            }

            // 处理升级带来的次数 +1
            if (this.upgraded) {
                effect += 1;
            }

            if (effect > 0) {
                for (int i = 0; i < effect; i++) {
                    // 造成伤害
                    addToBot(new DamageAction(m, new DamageInfo(p, damage, damageType), AttackEffect.BLUNT_LIGHT));
                    // 获得裁决
                    addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, 1), 1));
                    // 获得荆棘
                    addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, 1), 1));
                }

                if (!this.freeToPlayOnce) {
                    this.p.energy.use(EnergyPanel.totalCount);
                }
            }
            this.isDone = true;
        }
    }
}