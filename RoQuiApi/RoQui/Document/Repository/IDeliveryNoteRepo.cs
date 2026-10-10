namespace RoQuiApi.RoQui.Document.Repository;

using RoQuiApi.RoQui.Document.Model;

public interface IDeliveryNoteRepo
{
    DeliveryNote? GetDeliveryNoteByCodeAndNumber(string code, string number);

    void DeleteDeliveryNote(DeliveryNote deliveryNote);
    void CreateDeliveryNote(DeliveryNote deliveryNote);
    bool SaveChanges();
}
