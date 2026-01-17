package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.character.Penance;
import thePenance.powers.SceneInvestigationPower;
import thePenance.util.CardStats;

public class SceneInvestigation extends BaseCard {

    public static final String ID = makeID("SceneInvestigation");

    private static final int COST = 1;
    private static final int ENERGY_AMT = 2;

    public SceneInvestigation() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));

        setMagic(ENERGY_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

        // 2. 检查玩家是否已有该能力，手动更新“非层数”的属性
        //这是必须的，因为 ApplyPowerAction 只会叠加 amount (层数)，不会更新 boolean 和 int 字段
        if (p.hasPower(SceneInvestigationPower.POWER_ID)) {
            AbstractPower pow = p.getPower(SceneInvestigationPower.POWER_ID);
            if (pow instanceof SceneInvestigationPower) {
                ((SceneInvestigationPower) pow).updateStats(magicNumber, this.upgraded);
            }
        }
        addToBot(new ApplyPowerAction(p, p, new SceneInvestigationPower(p, 1, magicNumber, this.upgraded), 1));
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}