import LegalPage from "@/components/LegalPage";
import { TRADE_SECTIONS, SITE } from "@/content/legal";

const TradeDisclaimer = () => (
  <LegalPage
    title="Trade & Swap Disclaimer"
    description={`${SITE.name}'s trade board is peer-to-peer. No escrow, no guarantee, no fee. All trades are at your own risk.`}
    intro={`The ${SITE.name} trade board is a place to list specimens you want to swap and find other collectors. Every trade that results is a private arrangement between two users. ${SITE.name} is not a party to any trade.`}
    sections={TRADE_SECTIONS}
  />
);

export default TradeDisclaimer;
