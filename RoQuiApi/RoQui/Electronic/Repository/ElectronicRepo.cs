namespace RoQuiApi.RoQui.Electronic.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Electronic.Model;

public class ElectronicRepo : IElectronicRepo
{
    private readonly AppDbContext _context;

    public ElectronicRepo(AppDbContext context)
    {
        _context = context;
    }

    public string GetElectronicByCodeAndNumber(string code, string number)
    {
        var electronic = _context.Electronics.FirstOrDefault(e => e.Code == code && e.Number == number);
        if (electronic == null)
        {
            return "NO ENVIADO";
        }
        return electronic.Status ?? "NO ENVIADO";
    }

    public Parameter? GetParameterByName(string name)
    {
        return _context.Parameters.FirstOrDefault(p => p.Name == name);
    }
}