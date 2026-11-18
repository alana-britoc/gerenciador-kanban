// Observer no front: o quadro escuta os movimentos de cartão que o back
// publica por WebSocket (QuadroTempoRealObserver).
const URL_WS = `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws/quadro`
const ESPERA_MAXIMA_MS = 10_000

/**
 * Chama `aoMudar` sempre que o quadro mudou em outra aba, e também ao reconectar,
 * porque eventos podem ter sido perdidos enquanto a conexão estava fora.
 * Devolve a função que encerra a escuta.
 */
export function escutarQuadro(aoMudar) {
  let ws
  let tentativas = 0
  let timer
  let encerrado = false

  const conectar = () => {
    ws = new WebSocket(URL_WS)
    ws.onopen = () => {
      if (tentativas > 0) aoMudar()
      tentativas = 0
    }
    ws.onmessage = (e) => {
      let evento
      try {
        evento = JSON.parse(e.data)
      } catch {
        return
      }
      if (evento.evento === 'CARTAO_MOVIDO') aoMudar(evento)
    }
    ws.onclose = () => {
      if (encerrado) return
      // tenta de novo esperando cada vez mais: 1s, 2s, 4s... até 10s
      const espera = Math.min(1000 * 2 ** tentativas, ESPERA_MAXIMA_MS)
      tentativas++
      timer = setTimeout(conectar, espera)
    }
  }

  conectar()

  return () => {
    encerrado = true
    clearTimeout(timer)
    // fechar durante o handshake gera aviso no console (acontece no StrictMode)
    if (ws.readyState === WebSocket.CONNECTING) ws.onopen = () => ws.close()
    else ws.close()
  }
}
