using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class Spawner : MonoBehaviour
{

    [SerializeField]
    GameObject TilePrueba, bottomTile, startButton;

    TMP_Text scoreText;
    List<GameObject> stack;

    bool hasGameStarted, hasGameFinished;

    List<Color32> spectrum = new List<Color32>(){
    new Color32(204, 42, 30, 255),    // RAL 3000 - Rojo vivo
    new Color32(31, 91, 153, 255),    // RAL 5010 - Azul genciana
    new Color32(43, 97, 153, 255),    // RAL 5015 - Azul cielo
    new Color32(29, 92, 53, 255),     // RAL 6005 - Verde musgo
    new Color32(74, 124, 63, 255),    // RAL 6011 - Verde reseda
    new Color32(232, 168, 56, 255),   // RAL 1003 - Amarillo señal
    new Color32(224, 107, 31, 255),   // RAL 2004 - Naranja puro
    new Color32(133, 133, 133, 255),  // RAL 7004 - Gris señal
    new Color32(91, 103, 113, 255),   // RAL 7016 - Gris antracita
    new Color32(139, 46, 22, 255),    // RAL 8004 - Marrón cobre
    new Color32(107, 45, 107, 255),   // RAL 4007 - Violeta púrpura
    new Color32(26, 26, 26, 255),     // RAL 9005 - Negro intenso
};
    int modifier;
    int colorIndex;

    public static Spawner instance;

    private void Awake()
    {
        if (instance == null)
            instance = this;
        else
            Destroy(gameObject);
    }

    void SetTileColor(GameObject tile, Color32 color)
    {
        Renderer[] renderers = tile.GetComponentsInChildren<Renderer>(false); // false = solo activos
        foreach (Renderer rend in renderers)
        {
            foreach (Material mat in rend.materials)
            {
                if (mat.HasProperty("_BaseColor"))
                    mat.SetColor("_BaseColor", color);
                else if (mat.HasProperty("_Color"))
                    mat.SetColor("_Color", color);
            }
        }
    }

    // Start is called before the first frame update
    void Start()
    {
        scoreText = GameObject.Find("Score").GetComponent<TMP_Text>();
        stack = new List<GameObject>();
        hasGameFinished = false;
        hasGameStarted = false;
        modifier = 1;
        colorIndex = 0;
        stack.Add(bottomTile);
        SetTileColor(stack[0], spectrum[0]);
        CreateTile();
    }

    // Update is called once per frame
    void Update()
    {
        if (hasGameFinished || !hasGameStarted) return;
        if (Input.GetMouseButtonDown(0))
        {
            if (stack.Count > 1)
                stack[stack.Count - 1].GetComponent<Tile>().ScaleTile();
            if (hasGameFinished) return;
            StartCoroutine(MoveCamera());
            scoreText.text = (stack.Count - 1).ToString();
            CreateTile();
        }
    }

    IEnumerator MoveCamera()
    {
        float duration = 0.5f; // Cuánto tarda en subir la cámara
        float elapsed = 0f;
        Vector3 startPos = Camera.main.transform.position;
        Vector3 endPos = startPos + new Vector3(0, 1f, 0);

        while (elapsed < duration)
        {
            Camera.main.transform.position = Vector3.Lerp(startPos, endPos, elapsed / duration);
            elapsed += Time.deltaTime;
            yield return null; // Espera al siguiente frame, mucho más suave
        }
        Camera.main.transform.position = endPos;
    }

    void CreateTile()
    {
        GameObject previousTile = stack[stack.Count - 1];
        GameObject activeTile;

        activeTile = Instantiate(TilePrueba);
        stack.Add(activeTile);

        if (stack.Count > 2)
            activeTile.transform.localScale = previousTile.transform.localScale;

        activeTile.transform.position = new Vector3(previousTile.transform.position.x,
            previousTile.transform.position.y + previousTile.transform.localScale.y, previousTile.transform.position.z);

        colorIndex += modifier;
        if (colorIndex == spectrum.Count || colorIndex == -1)
        {
            modifier *= -1;
            colorIndex += 2 * modifier;
        }

        SetTileColor(activeTile, spectrum[colorIndex]);
        activeTile.GetComponent<Tile>().moveX = stack.Count % 2 == 0;
    }

    public void GameOver()
    {
        startButton.SetActive(true);
        hasGameFinished = true;
        StartCoroutine(EndCamera());
    }

    IEnumerator EndCamera()
    {
        GameObject camera = GameObject.FindGameObjectWithTag("MainCamera");
        Vector3 temp = camera.transform.position;
        Vector3 final = new Vector3(temp.x, temp.y - stack.Count * 0.5f, temp.z);
        float cameraSizeFinal = stack.Count * 0.65f;
        while (camera.GetComponent<Camera>().orthographicSize < cameraSizeFinal)
        {
            camera.GetComponent<Camera>().orthographicSize += 0.2f;
            temp = camera.transform.position;
            temp = Vector3.Lerp(temp, final, 0.2f);
            camera.transform.position = temp;
            yield return new WaitForSeconds(0.01f);
        }
        camera.transform.position = final;
    }

    public void StartButton()
    {
        if (hasGameFinished)
        {
            UnityEngine.SceneManagement.SceneManager.LoadScene(1);
        }
        else
        {
            startButton.SetActive(false);
            hasGameStarted = true;
        }
    }
}