import LegalPage from "@/components/LegalPage";
import { SAFETY_SECTIONS, SITE } from "@/content/legal";

const Safety = () => (
  <LegalPage
    title="Safety & Meetup Notice"
    description={`How to stay safe when meeting another ${SITE.name} user in person to trade specimens. Meet in public, bring a friend, trust your instincts.`}
    intro={`${SITE.name}'s trade board, trip planner, and community features can lead to in-person meetings with people you do not know. Meeting a stranger to exchange specimens carries real risk. This notice is here so you can make safe choices.`}
    sections={SAFETY_SECTIONS}
  />
);

export default Safety;
