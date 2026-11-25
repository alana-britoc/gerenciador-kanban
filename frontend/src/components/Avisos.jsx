import { CircleCheck, X } from 'lucide-react'

export default function Avisos({ avisos, onFechar }) {
  return (
    <div className="avisos" role="status" aria-live="polite">
      {avisos.map((aviso) => (
        <div key={aviso.id} className="aviso">
          <CircleCheck size={18} />
          <p>{aviso.texto}</p>
          <button type="button" aria-label="Fechar aviso" onClick={() => onFechar(aviso.id)}>
            <X size={14} />
          </button>
        </div>
      ))}
    </div>
  )
}
