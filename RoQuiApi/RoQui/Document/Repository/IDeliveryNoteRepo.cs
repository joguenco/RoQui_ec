namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.RoQui.Invoice.Model;

public interface IDeliveryNoteRepo
{
    DeliveryNote? GetDeliveryNoteByCodeAndNumber(string code, string number);

    void DeleteDeliveryNote(DeliveryNote deliveryNote);
    void CreateDeliveryNote(DeliveryNote deliveryNote);
    bool SaveChanges();
}
