-- Aplicar despues de realizar un respaldo. No elimina ni corrige datos existentes.
-- NOT VALID permite revisar registros heredados por separado. Nuevas escrituras si se validan.
BEGIN;
DO $$
DECLARE r RECORD; definicion TEXT;
BEGIN
  FOR r IN SELECT c.conname, c.conrelid::regclass AS tabla, pg_get_constraintdef(c.oid) AS definicion
    FROM pg_constraint c
    WHERE c.contype='f' AND c.confdeltype='c'
      AND c.conrelid IN ('grupo'::regclass,'inscripcion_curso'::regclass,'usuario'::regclass)
  LOOP
    definicion := replace(r.definicion, 'ON DELETE CASCADE', 'ON DELETE RESTRICT');
    EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I',r.tabla,r.conname);
    EXECUTE format('ALTER TABLE %s ADD CONSTRAINT %I %s',r.tabla,r.conname,definicion);
  END LOOP;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='ck_creditos_entrega' AND conrelid='materia'::regclass) THEN
    ALTER TABLE materia ADD CONSTRAINT ck_creditos_entrega CHECK (creditos BETWEEN 1 AND 10) NOT VALID;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='ck_estado_nota_entrega' AND conrelid='inscripcion_curso'::regclass) THEN
    ALTER TABLE inscripcion_curso ADD CONSTRAINT ck_estado_nota_entrega CHECK (
      estado='Cancelado' OR (nota_final IS NULL AND estado='Matriculado') OR
      (nota_final IS NOT NULL AND nota_final >= 3 AND nota_final <= 5 AND estado='Aprobado') OR
      (nota_final IS NOT NULL AND nota_final >= 0 AND nota_final < 3 AND estado='Reprobado')) NOT VALID;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='ck_usuario_vinculo_entrega' AND conrelid='usuario'::regclass) THEN
    ALTER TABLE usuario ADD CONSTRAINT ck_usuario_vinculo_entrega CHECK (
      (rol='ADMIN' AND id_estudiante IS NULL AND id_docente IS NULL) OR
      (rol='ESTUDIANTE' AND id_estudiante IS NOT NULL AND id_docente IS NULL) OR
      (rol='DOCENTE' AND id_docente IS NOT NULL AND id_estudiante IS NULL)) NOT VALID;
  END IF;
END $$;
-- Si hay correos duplicados ignorando mayusculas, esta instruccion falla y toda la migracion se revierte.
CREATE UNIQUE INDEX IF NOT EXISTS uq_estudiante_email_normalizado ON estudiante (LOWER(email));
COMMIT;
