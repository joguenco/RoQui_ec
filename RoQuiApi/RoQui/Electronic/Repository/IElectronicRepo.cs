namespace RoQuiApi.RoQui.Electronic.Repository;

using RoQuiApi.RoQui.Electronic.Model;

public interface IElectronicRepo
{
    Parameter? GetParameterByName(string name);
}
