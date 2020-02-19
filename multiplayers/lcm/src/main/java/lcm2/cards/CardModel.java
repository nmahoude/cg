package lcm2.cards;

import java.util.HashMap;
import java.util.Map;

import lcm2.CardType;

public class CardModel {
	private static Map<Integer, CardModel> models = new HashMap<>();
	
	public final int instanceId;
	public final int cardNumber;
	public final CardType type;
	public final int cost;
	public final int myHealthChange;
	public final int opponentHealthChange;
	public final int cardDraw;

	private CardModel(int instanceId, int cardNumber, int cardType, int cost, int myHealthChange, int opponentHealthChange,
			int cardDraw) {
		this.instanceId = instanceId;
		this.cardNumber = cardNumber;
		this.type = CardType.fromValue(cardType);
		this.cost = cost;
		this.myHealthChange = myHealthChange;
		this.opponentHealthChange = opponentHealthChange;
		this.cardDraw = cardDraw;
	}
	
	public static CardModel get(int instanceId, int cardNumber, int cardType, int cost, int myHealthChange, int opponentHealthChange,
			int cardDraw) {

		if (instanceId < 0) {
			return new CardModel(instanceId, cardNumber,cardType, cost, myHealthChange, opponentHealthChange, cardDraw);
		}
		CardModel model = models.get(instanceId);
		if (model == null) {
			model = new CardModel(instanceId, cardNumber,cardType, cost, myHealthChange, opponentHealthChange, cardDraw);
			models.put(instanceId, model);
		}
		return model;
	}

}
