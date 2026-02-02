package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

public class Sting extends BaseCard {

    public static final String ID = makeID("Sting");

    private static final int COST = 0;
    private static final int HP_COST = 3;
    private static final int THORNS_AMT = 3;
    private static final int UPGRADE_THORNS_AMT = 3;
    private static final int JUDGE_AMT = 2;
    private static final int UPGRADE_JUDGE_AMT = 2; // 2 -> 4

    public Sting() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.COMMON,
                CardTarget.SELF,
                COST
        ));

        // MagicNumber 控制荆棘
        setMagic(THORNS_AMT, UPGRADE_THORNS_AMT);

        // 自定义变量 "Judge" 控制裁决获得量
        // 在描述中请使用 !thePenance:Judge!
        setCustomVar("Judge", JUDGE_AMT, UPGRADE_JUDGE_AMT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 扣血
        addToBot(new LoseHPAction(p, p, HP_COST));

        // 获得荆棘 (Magic)
        addToBot(new ApplyPowerAction(p, p, new ThornAuraPower(p, magicNumber), magicNumber));

        // 获得裁决 (自定义变量)
        int judgeGain = customVar("Judge");
        addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, judgeGain), judgeGain));
    }
}