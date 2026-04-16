using System.Collections;
using UnityEngine;
using UnityEngine.SceneManagement;

public class IntroScreen : MonoBehaviour
{
    [SerializeField] float tiempoAutoSkip = 5f; // segundos antes de pasar solo

    bool touched = false;

    void Start()
    {
        StartCoroutine(AutoSkip());
    }

    void Update()
    {
        if (!touched && (Input.GetMouseButtonDown(0) || Input.touchCount > 0))
        {
            touched = true;
            CargarJuego();
        }
    }

    IEnumerator AutoSkip()
    {
        yield return new WaitForSeconds(tiempoAutoSkip);
        CargarJuego();
    }

    void CargarJuego()
    {
        SceneManager.LoadScene(1); // tu escena de juego
    }
}
