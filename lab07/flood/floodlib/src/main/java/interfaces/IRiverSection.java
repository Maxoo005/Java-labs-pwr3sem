package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRiverSection extends INaming {
    void setRealDischarge(int realDischarge) throws RemoteException; // ustawia rzeczywistą wielkość zrzutu wody ze zbiornika znajdującego się na początku odcinka rzecznego,
    void setRainfall(int rainfall) throws RemoteException; // ustawia wielkość opadów atmosferycznych,

    void assignRetensionBasin(IRetensionBasin irb) throws RemoteException; // ustawia namiastkę zbiornika retencyjnego na wyjściu;
}

